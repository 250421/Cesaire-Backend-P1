package com.authenthication.auth_session.UserController;

import com.authenthication.auth_session.Dto.LoginDto;
import com.authenthication.auth_session.Dto.UserDto;
import com.authenthication.auth_session.Service.UserService;
import com.authenthication.auth_session.Service.Implementation.UserImpl;
import com.authenthication.auth_session.response.AddUserResponse;
import com.authenthication.auth_session.response.LoginResponse;
import com.authenthication.auth_session.response.MessageResponse;

import com.authenthication.auth_session.Dto.ProductsDto;
import com.authenthication.auth_session.Entity.Products;
import com.authenthication.auth_session.Repository.ProductsRepo;
import com.authenthication.auth_session.response.AddProductsResponse;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.List;
import org.springframework.util.StringUtils;


import com.authenthication.auth_session.Entity.SessionInfo;
import com.authenthication.auth_session.Entity.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")    
@RequestMapping("api/auth")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private ProductsRepo productsRepo;   

    @PostMapping(path = "/sign-up")
    public ResponseEntity<?> registerUser(@RequestBody UserDto userDto) {
        AddUserResponse response = userService.addUser(userDto);
        
        if (response.getStatus()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }
        
        // Handle specific error cases
        if (response.getMessage().contains("cannot be empty")) {
            return ResponseEntity.badRequest().body(response);
        }
        if (response.getMessage().contains("already registered")) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }
        if (response.getMessage().contains("too long") || 
            response.getMessage().contains("Invalid email") || 
            response.getMessage().contains("at least 8 characters")) {
            return ResponseEntity.badRequest().body(response);
        }
        
        return ResponseEntity.internalServerError().body(response);
    }

    @PostMapping(path = "/sign-in")
    public ResponseEntity<?> loginUser(@RequestBody LoginDto loginDto, HttpServletResponse response) {
        LoginResponse loginResponse = userService.loginUser(loginDto);
        
        if (loginResponse.getStatus()) {
            // Set the session ID as a cookie
            Cookie sessionCookie = new Cookie("SESSION_ID", loginResponse.getSessionId());
            sessionCookie.setHttpOnly(true);
            sessionCookie.setSecure(true);
            sessionCookie.setPath("/");
            sessionCookie.setMaxAge(60 * 30);
            response.addCookie(sessionCookie);
            return ResponseEntity.ok(loginResponse);
        }
        
        if (loginResponse.getMessage().equals("Email does not exist")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(loginResponse);
        }
        if (loginResponse.getMessage().equals("Password does not match")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(loginResponse);
        }
        
        return ResponseEntity.badRequest().body(loginResponse);
    }

    @PostMapping(path = "/sign-out")
    public ResponseEntity<?> logoutUser(HttpServletRequest request, HttpServletResponse response) {
        String sessionId = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("SESSION_ID".equals(cookie.getName())) {
                    sessionId = cookie.getValue();
                    break;
                }
            }
        }
        
        if (sessionId != null && ((UserImpl)userService).logoutUser(sessionId)) {
            // Invalidate the cookie
            Cookie sessionCookie = new Cookie("SESSION_ID", null);
            sessionCookie.setHttpOnly(true);
            sessionCookie.setSecure(true);
            sessionCookie.setPath("/");
            sessionCookie.setMaxAge(0);
            response.addCookie(sessionCookie);
            
            return ResponseEntity.ok(new MessageResponse("Logout successful"));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse("Invalid session"));
    }

    @GetMapping(path = "/sessions")
    public ResponseEntity<?> getActiveSessions(HttpServletRequest request) {
        String sessionId = request.getHeader("X-Session-ID");
        if (sessionId == null) {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("SESSION_ID".equals(cookie.getName())) {
                        sessionId = cookie.getValue();
                        break;
                    }
                }
            }
        }
        
        Optional<User> userOpt = ((UserImpl)userService).getUserFromSession(sessionId);
        if (!userOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new MessageResponse("Invalid session"));
        }
        
        User user = userOpt.get();
        List<SessionInfo> sessions = ((UserImpl)userService).getAllActiveSessionsForUser(user.getUserid());
        
        List<Map<String, Object>> response = sessions.stream()
            .map(session -> {
                Map<String, Object> sessionMap = new HashMap<>();
                sessionMap.put("sessionId", session.getSessionId());
                sessionMap.put("createdAt", session.getCreatedAt());
                sessionMap.put("expiresAt", session.getExpiresAt());
                return sessionMap;
            })
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/current")
public ResponseEntity<?> checkAuth(HttpServletRequest request) {
    String sessionId = request.getHeader("X-Session-ID");
    if (sessionId == null) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("SESSION_ID".equals(cookie.getName())) {
                    sessionId = cookie.getValue();
                    break;
                }
            }
        }
    }
    
    if (sessionId == null) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    Optional<User> userOpt = ((UserImpl)userService).getUserFromSession(sessionId);
    
    if (userOpt.isPresent()) {
        User user = userOpt.get();
        Map<String, Object> response = new HashMap<>();
        response.put("id", user.getUserid());
        response.put("username", user.getUsername());
        response.put("email", user.getEmail());
        response.put("role", user.getRole());
        return ResponseEntity.ok(response);
    }
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
}

@PostMapping("/products")
    public ResponseEntity<?> createProduct(@RequestBody ProductsDto productsDto, HttpServletRequest request) {
        String sessionId = getSessionIdFromRequest(request);
        if (sessionId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(new MessageResponse("Unauthorized"));
        }

        
        if (!((UserImpl)userService).isAdmin(sessionId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new MessageResponse("Only ADMIN can create products"));
        }

        // Validate required fields
        if (!StringUtils.hasText(productsDto.getName()) || 
            productsDto.getStock() == null || 
            productsDto.getPrice() == null) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Required fields are missing"));
        }

        if (!((UserImpl)userService).validateProductNumbers(productsDto)) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Stock and price cannot be negative"));
        }

        Optional<User> userOpt = ((UserImpl)userService).getUserFromSession(sessionId);
        if (!userOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User creator = userOpt.get();
        Products product = new Products();
        product.setName(productsDto.getName());
        product.setStock(productsDto.getStock());
        product.setPrice(productsDto.getPrice());
        product.setImageUrl(productsDto.getImageUrl());
        product.setReview(productsDto.getReview());
        product.setCreatedBy(creator);

        Products savedProduct = productsRepo.save(product);

        return ResponseEntity.ok(new AddProductsResponse(
            savedProduct.getUserid(),
            savedProduct.getName(),
            savedProduct.getStock(),
            creator.getUserid(),
            "Product created successfully",
            true
        ));
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<?> updateProduct(
            @PathVariable Long id,
            @RequestBody ProductsDto productsDto,
            HttpServletRequest request) {
        String sessionId = getSessionIdFromRequest(request);
        if (sessionId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Optional<User> userOpt = ((UserImpl)userService).getUserFromSession(sessionId);
        if (!userOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Optional<Products> productOpt = productsRepo.findById(id);
        if (!productOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Products product = productOpt.get();
        User currentUser = userOpt.get();

        if (((UserImpl)userService).isAdmin(sessionId)) {
            // ADMIN can update all fields
            if (!((UserImpl)userService).validateProductNumbers(productsDto)) {
                return ResponseEntity.badRequest()
                        .body(new MessageResponse("Stock and price cannot be negative"));
            }
            
            product.setName(productsDto.getName());
            product.setStock(productsDto.getStock());
            product.setPrice(productsDto.getPrice());
            product.setImageUrl(productsDto.getImageUrl());
            product.setReview(productsDto.getReview());
        } else {
            // USER can only update review
            if (productsDto.getReview() == null) {
                return ResponseEntity.badRequest()
                        .body(new MessageResponse("Review cannot be empty"));
            }
            product.setReview(productsDto.getReview());
        }

        Products updatedProduct = productsRepo.save(product);
        return ResponseEntity.ok(new AddProductsResponse(
            updatedProduct.getUserid(),
            updatedProduct.getName(),
            updatedProduct.getStock(),
            currentUser.getUserid(),
            "Product updated successfully",
            true
        ));
    }

    private String getSessionIdFromRequest(HttpServletRequest request) {
        String sessionId = request.getHeader("X-Session-ID");
        if (sessionId == null) {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("SESSION_ID".equals(cookie.getName())) {
                        sessionId = cookie.getValue();
                        break;
                    }
                }
            }
        }
        return sessionId;
    }


// Modified GET endpoint to match frontend expectations
@GetMapping("/products")
public ResponseEntity<List<Map<String, Object>>> getAllProducts(
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "10") int size,
HttpServletRequest request) {
    // Check authentication
    String sessionId = getSessionIdFromRequest(request);
    if (sessionId == null) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    // Verify valid session
    Optional<User> userOpt = ((UserImpl)userService).getUserFromSession(sessionId);
    if (!userOpt.isPresent()) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    // Get all products and format for frontend
    List<Products> products = productsRepo.findAll();
    
    List<Map<String, Object>> response = products.stream()
        .map(product -> {
            Map<String, Object> productMap = new HashMap<>();
            productMap.put("id", product.getUserid()); // Note: Consider renaming to productId
            productMap.put("name", product.getName());
            productMap.put("stock", product.getStock());
            productMap.put("price", product.getPrice());
            
            // Optional fields
            if (product.getImageUrl() != null) {
                productMap.put("imageUrl", product.getImageUrl());
            }
            if (product.getReview() != null) {
                productMap.put("review", product.getReview());
            }
            
            return productMap;
        })
        .collect(Collectors.toList());

    return ResponseEntity.ok(response);
}


@GetMapping("/products/{id}")
public ResponseEntity<?> getProductById(
        @PathVariable Long id,
        HttpServletRequest request) {
    // Check authentication
    String sessionId = getSessionIdFromRequest(request);
    if (sessionId == null) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    // Verify valid session
    Optional<User> userOpt = ((UserImpl)userService).getUserFromSession(sessionId);
    if (!userOpt.isPresent()) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    // Find product
    Optional<Products> productOpt = productsRepo.findById(id);
    if (!productOpt.isPresent()) {
        return ResponseEntity.notFound().build();
    }

    Products product = productOpt.get();
    
    // Format response to match frontend expectations
    Map<String, Object> response = new HashMap<>();
    response.put("id", product.getUserid());
    response.put("name", product.getName());
    response.put("stock", product.getStock());
    response.put("price", product.getPrice());
    if (product.getImageUrl() != null) {
        response.put("imageUrl", product.getImageUrl());
    }
    if (product.getReview() != null) {
        response.put("review", product.getReview());
    }

    return ResponseEntity.ok(response);
}

@PutMapping("/products/{id}")
public ResponseEntity<?> updateProduct(
        @PathVariable Long id,
        @RequestBody Map<String, Object> updates,
        HttpServletRequest request) {
    String sessionId = getSessionIdFromRequest(request);
    if (sessionId == null) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    Optional<User> userOpt = ((UserImpl)userService).getUserFromSession(sessionId);
    if (!userOpt.isPresent()) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    Optional<Products> productOpt = productsRepo.findById(id);
    if (!productOpt.isPresent()) {
        return ResponseEntity.notFound().build();
    }

    Products product = productOpt.get();

    if (((UserImpl)userService).isAdmin(sessionId)) {
        // ADMIN can update all fields
        if (updates.containsKey("name")) {
            product.setName((String) updates.get("name"));
        }
        if (updates.containsKey("stock")) {
            product.setStock(((Number) updates.get("stock")).intValue());
        }
        if (updates.containsKey("price")) {
            product.setPrice(((Number) updates.get("price")).doubleValue());
        }
        if (updates.containsKey("imageUrl")) {
            product.setImageUrl((String) updates.get("imageUrl"));
        }
        if (updates.containsKey("review")) {
            product.setReview((String) updates.get("review"));
        }
    } else {
        // USER can only update review
        if (updates.containsKey("review")) {
            product.setReview((String) updates.get("review"));
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new MessageResponse("Users can only update reviews"));
        }
    }
    Products updatedProduct = productsRepo.save(product);
    return ResponseEntity.ok(updatedProduct);
}


@DeleteMapping("/products/{id}")
public ResponseEntity<?> deleteProduct(
        @PathVariable Long id,
        HttpServletRequest request) {
    String sessionId = getSessionIdFromRequest(request);
    if (sessionId == null) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    if (!((UserImpl)userService).isAdmin(sessionId)) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new MessageResponse("Only ADMIN can delete products"));
    }

    if (!productsRepo.existsById(id)) {
        return ResponseEntity.notFound().build();
    }

    productsRepo.deleteById(id);
    return ResponseEntity.ok().build();
}

}

