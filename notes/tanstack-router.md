# TanStack Router Basics (with TypeScript)

## What is TanStack Router?

TanStack Router is a modern, type-safe router for React applications. Key benefits include:

- Full TypeScript support with end-to-end type safety
- First-class search params handling
- Data loading and mutations built-in
- Nested layouts and routes
- Route-based code-splitting

## Setting Up

First, install the necessary packages:

```bash
# Install TanStack Router
npm install @tanstack/react-router
```

## Basic Setup

Let's set up a basic router configuration:

```tsx
// src/router.tsx
import {
  createRootRoute,
  createRoute,
  createRouter,
} from "@tanstack/react-router";
import { TanStackRouterDevtools } from "@tanstack/router-devtools";

// Import your pages
import HomePage from "./pages/Home";
import AboutPage from "./pages/About";
import NotFoundPage from "./pages/NotFound";

// Create a root layout route
export const rootRoute = createRootRoute({
  component: () => (
    <div className="min-h-screen flex flex-col">
      <header className="bg-gray-800 text-white p-4">
        <h1>My App</h1>
        <nav>
          <ul className="flex gap-4">
            <li>
              <a href="/">Home</a>
            </li>
            <li>
              <a href="/about">About</a>
            </li>
          </ul>
        </nav>
      </header>

      <main className="flex-1 p-4">
        {/* Outlet is where child routes will render */}
        <Outlet />
      </main>

      <footer className="bg-gray-800 text-white p-4">
        <p>© 2023 My App</p>
      </footer>

      {/* Show devtools in development */}
      {import.meta.env.DEV && <TanStackRouterDevtools />}
    </div>
  ),
});

// Define routes
export const homeRoute = createRoute({
  getParentRoute: () => rootRoute,
  path: "/",
  component: HomePage,
});

export const aboutRoute = createRoute({
  getParentRoute: () => rootRoute,
  path: "/about",
  component: AboutPage,
});

// Define a catch-all route for 404 pages
export const notFoundRoute = createRoute({
  getParentRoute: () => rootRoute,
  path: "*",
  component: NotFoundPage,
});

// Create the route tree
const routeTree = rootRoute.addChildren([homeRoute, aboutRoute, notFoundRoute]);

// Create and export the router
export const router = createRouter({
  routeTree,
  defaultPreloadStaleTime: 0,
  defaultPreload: "intent",
});

// Register router for typesafety
declare module "@tanstack/react-router" {
  interface Register {
    router: typeof router;
  }
}
```

Then, use the router in your main app file:

```tsx
// src/main.tsx
import React from "react";
import ReactDOM from "react-dom/client";
import { RouterProvider } from "@tanstack/react-router";
import { router } from "./router";

ReactDOM.createRoot(document.getElementById("root")!).render(
  <React.StrictMode>
    <RouterProvider router={router} />
  </React.StrictMode>
);
```

## Creating Basic Pages

Here's what simple pages might look like:

```tsx
// src/pages/Home.tsx
export default function HomePage() {
  return (
    <div>
      <h1 className="text-2xl font-bold">Welcome Home!</h1>
      <p>This is the home page of our application.</p>
    </div>
  );
}

// src/pages/About.tsx
export default function AboutPage() {
  return (
    <div>
      <h1 className="text-2xl font-bold">About Us</h1>
      <p>Learn more about our company and mission.</p>
    </div>
  );
}

// src/pages/NotFound.tsx
export default function NotFoundPage() {
  return (
    <div>
      <h1 className="text-2xl font-bold">404 - Page Not Found</h1>
      <p>Sorry, the page you are looking for does not exist.</p>
    </div>
  );
}
```

## Links and Navigation

Use the Link component for navigation:

```tsx
import { Link } from "@tanstack/react-router";

function Navigation() {
  return (
    <nav>
      <ul className="flex gap-4">
        <li>
          <Link to="/" className="text-blue-500 hover:underline">
            Home
          </Link>
        </li>
        <li>
          <Link to="/about" className="text-blue-500 hover:underline">
            About
          </Link>
        </li>
      </ul>
    </nav>
  );
}
```

For programmatic navigation:

```tsx
import { useNavigate } from "@tanstack/react-router";

function LoginButton() {
  const navigate = useNavigate();

  const handleLogin = async () => {
    // Perform login logic...
    const success = await performLogin();

    if (success) {
      // Redirect after successful login
      navigate({ to: "/dashboard" });
    }
  };

  return <button onClick={handleLogin}>Log In</button>;
}
```

## Route Parameters

Routes can have dynamic parameters:

```tsx
// src/router.tsx
export const userRoute = createRoute({
  getParentRoute: () => rootRoute,
  path: "/users/$userId",
  component: UserPage,
});

// src/pages/User.tsx
import { useParams } from "@tanstack/react-router";

export default function UserPage() {
  // Access route parameters with proper typing
  const { userId } = useParams();

  return (
    <div>
      <h1 className="text-2xl font-bold">User Profile</h1>
      <p>User ID: {userId}</p>
      {/* Fetch and display user details */}
    </div>
  );
}
```

## Nested Routes

You can create nested routes and layouts:

```tsx
// src/router.tsx
// Dashboard parent layout route
export const dashboardRoute = createRoute({
  getParentRoute: () => rootRoute,
  path: "/dashboard",
  component: DashboardLayout,
});

// Dashboard child routes
export const dashboardHomeRoute = createRoute({
  getParentRoute: () => dashboardRoute,
  path: "/", // This makes it /dashboard/
  component: DashboardHome,
});

export const dashboardSettingsRoute = createRoute({
  getParentRoute: () => dashboardRoute,
  path: "/settings", // This makes it /dashboard/settings
  component: DashboardSettings,
});

// Add child routes to the route tree
const routeTree = rootRoute.addChildren([
  homeRoute,
  aboutRoute,
  dashboardRoute.addChildren([dashboardHomeRoute, dashboardSettingsRoute]),
  notFoundRoute,
]);

// src/layouts/DashboardLayout.tsx
import { Link, Outlet } from "@tanstack/react-router";

export default function DashboardLayout() {
  return (
    <div className="flex">
      {/* Sidebar navigation */}
      <aside className="w-64 bg-gray-100 p-4">
        <h2 className="font-bold mb-4">Dashboard</h2>
        <nav>
          <ul className="space-y-2">
            <li>
              <Link to="/dashboard" className="text-blue-500 hover:underline">
                Dashboard Home
              </Link>
            </li>
            <li>
              <Link
                to="/dashboard/settings"
                className="text-blue-500 hover:underline"
              >
                Settings
              </Link>
            </li>
          </ul>
        </nav>
      </aside>

      {/* Main content area */}
      <main className="flex-1 p-6">
        <Outlet /> {/* Child routes render here */}
      </main>
    </div>
  );
}
```

## Search Parameters

TanStack Router has first-class support for search parameters:

```tsx
// src/pages/Products.tsx
import { Link, useSearch } from "@tanstack/react-router";

// Define the search params type
interface ProductsSearchParams {
  category?: string;
  sort?: "price" | "name" | "newest";
  page?: number;
}

export const productsRoute = createRoute({
  getParentRoute: () => rootRoute,
  path: "/products",
  validateSearch: (search: Record<string, unknown>): ProductsSearchParams => {
    // Validate and transform search parameters
    return {
      category: search.category as string | undefined,
      sort: search.sort as "price" | "name" | "newest" | undefined,
      page: search.page ? Number(search.page) : 1,
    };
  },
  component: ProductsPage,
});

function ProductsPage() {
  // Get typed search parameters
  const { category, sort, page } = useSearch({ from: productsRoute.id });

  return (
    <div>
      <h1>Products</h1>

      {/* Display current filters */}
      <div>
        <p>Category: {category || "All"}</p>
        <p>Sort by: {sort || "Default"}</p>
        <p>Page: {page}</p>
      </div>

      {/* Filter links */}
      <div className="flex gap-2 mt-4">
        <Link
          search={{ category: "electronics", sort, page }}
          className="px-2 py-1 bg-blue-100 rounded"
        >
          Electronics
        </Link>
        <Link
          search={{ category: "clothing", sort, page }}
          className="px-2 py-1 bg-blue-100 rounded"
        >
          Clothing
        </Link>
      </div>

      {/* Sort options */}
      <div className="flex gap-2 mt-4">
        <Link
          search={{ category, sort: "price", page }}
          className="px-2 py-1 bg-gray-100 rounded"
        >
          Sort by Price
        </Link>
        <Link
          search={{ category, sort: "name", page }}
          className="px-2 py-1 bg-gray-100 rounded"
        >
          Sort by Name
        </Link>
      </div>

      {/* Pagination */}
      <div className="flex gap-2 mt-4">
        <Link
          search={{ category, sort, page: Math.max(1, page - 1) }}
          className="px-2 py-1 bg-gray-200 rounded"
        >
          Previous
        </Link>
        <Link
          search={{ category, sort, page: page + 1 }}
          className="px-2 py-1 bg-gray-200 rounded"
        >
          Next
        </Link>
      </div>

      {/* Product list would go here */}
    </div>
  );
}
```

## Data Loading with Loaders

TanStack Router has built-in support for data loading:

```tsx
// src/pages/Products.tsx
import { axios } from "axios";

interface Product {
  id: number;
  name: string;
  price: number;
  category: string;
}

export const productsRoute = createRoute({
  getParentRoute: () => rootRoute,
  path: "/products",
  loader: async ({ search }) => {
    // Get the search params
    const { category, sort, page } = search;

    // Build query params
    const params = new URLSearchParams();
    if (category) params.append("category", category);
    if (sort) params.append("sort", sort);
    if (page) params.append("page", String(page));

    // Fetch data
    const response = await axios.get<Product[]>(
      `/api/products?${params.toString()}`
    );

    // Return the data
    return {
      products: response.data,
    };
  },
  component: ProductsPage,
});

function ProductsPage() {
  // Access the loader data
  const { products } = productsRoute.useLoaderData();

  return (
    <div>
      <h1>Products</h1>

      {/* Product list */}
      <div className="grid grid-cols-3 gap-4 mt-6">
        {products.map((product) => (
          <div key={product.id} className="border p-4 rounded">
            <h2 className="font-bold">{product.name}</h2>
            <p>${product.price.toFixed(2)}</p>
            <p>Category: {product.category}</p>
          </div>
        ))}
      </div>
    </div>
  );
}
```

## Error Handling

Handle route errors gracefully:

```tsx
// src/router.tsx
export const userRoute = createRoute({
  getParentRoute: () => rootRoute,
  path: "/users/$userId",
  loader: async ({ params }) => {
    try {
      const response = await axios.get(`/api/users/${params.userId}`);
      return { user: response.data };
    } catch (error) {
      // Throw a response error that the errorComponent can handle
      throw new Response("User not found", { status: 404 });
    }
  },
  // Specify an error component
  errorComponent: ({ error }) => <UserErrorPage error={error} />,
  component: UserPage,
});

// src/pages/UserError.tsx
import { Link } from "@tanstack/react-router";

interface UserErrorPageProps {
  error: Error | Response;
}

export default function UserErrorPage({ error }: UserErrorPageProps) {
  let message = "An unknown error occurred";
  let status = 500;

  // Handle Response errors
  if (error instanceof Response) {
    message = error.statusText;
    status = error.status;
  } else if (error instanceof Error) {
    message = error.message;
  }

  return (
    <div className="p-4 bg-red-50 rounded">
      <h1 className="text-2xl font-bold text-red-700">Error {status}</h1>
      <p className="mt-2">{message}</p>
      <Link to="/" className="mt-4 inline-block text-blue-500 hover:underline">
        Return to Home
      </Link>
    </div>
  );
}
```

## Route Guards (Authentication)

Create protected routes that redirect unauthenticated users:

```tsx
// src/router.tsx
import { redirect } from "@tanstack/react-router";
import { isAuthenticated } from "./auth";

export const dashboardRoute = createRoute({
  getParentRoute: () => rootRoute,
  path: "/dashboard",
  beforeLoad: async () => {
    // Check if user is authenticated
    if (!isAuthenticated()) {
      // Redirect to login page with a return URL
      throw redirect({
        to: "/login",
        search: {
          returnTo: "/dashboard",
        },
      });
    }
  },
  component: DashboardLayout,
});

// src/pages/Login.tsx
import { useNavigate, useSearch } from "@tanstack/react-query";
import { login } from "../auth";

interface LoginSearchParams {
  returnTo?: string;
}

export const loginRoute = createRoute({
  getParentRoute: () => rootRoute,
  path: "/login",
  validateSearch: (search: Record<string, unknown>): LoginSearchParams => {
    return {
      returnTo: search.returnTo as string | undefined,
    };
  },
  component: LoginPage,
});

function LoginPage() {
  const navigate = useNavigate();
  const { returnTo = "/" } = useSearch({ from: loginRoute.id });

  const handleLogin = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();

    // Get form data
    const formData = new FormData(e.currentTarget);
    const username = formData.get("username") as string;
    const password = formData.get("password") as string;

    // Perform login
    const success = await login(username, password);

    if (success) {
      // Redirect to the return URL
      navigate({ to: returnTo });
    }
  };

  return (
    <div>
      <h1>Login</h1>
      <form onSubmit={handleLogin}>{/* Form fields */}</form>
    </div>
  );
}
```

## File-Based Routing

TanStack Router supports file-based routing, which is a convenient way to define routes based on file structure:

```tsx
// src/routes/__root.tsx
import { createRootRoute } from "@tanstack/react-router";
import { TanStackRouterDevtools } from "@tanstack/router-devtools";

export const Route = createRootRoute({
  component: () => (
    <>
      <RootLayout>
        <Outlet />
      </RootLayout>
      {import.meta.env.DEV && <TanStackRouterDevtools />}
    </>
  ),
});

// src/routes/index.tsx
import { createFileRoute } from "@tanstack/react-router";

export const Route = createFileRoute("/")({
  component: () => <HomePage />,
});

// src/routes/about.tsx
import { createFileRoute } from "@tanstack/react-router";

export const Route = createFileRoute("/about")({
  component: () => <AboutPage />,
});

// src/routes/users/$userId.tsx
import { createFileRoute } from "@tanstack/react-router";
import axios from "axios";

export const Route = createFileRoute("/users/$userId")({
  loader: async ({ params }) => {
    const response = await axios.get(`/api/users/${params.userId}`);
    return { user: response.data };
  },
  component: () => {
    const { user } = Route.useLoaderData();
    return <UserPage user={user} />;
  },
});
```

## Integrating with TanStack Query

TanStack Router works well with TanStack Query for data fetching:

```tsx
// src/router.tsx
import { createRootRoute, createRouter } from "@tanstack/react-router";
import { QueryClient } from "@tanstack/react-query";

// Create a QueryClient
const queryClient = new QueryClient();

// Create the root route with query client in context
export const rootRoute = createRootRoute({
  // Pass the queryClient to the loader context
  context: {
    queryClient,
  },
});

// Create a route with TanStack Query integration
export const userRoute = createRoute({
  getParentRoute: () => rootRoute,
  path: "/users/$userId",
  loader: async ({ params, context }) => {
    // Use the queryClient to fetch data
    const user = await context.queryClient.fetchQuery({
      queryKey: ["user", params.userId],
      queryFn: () => fetchUser(params.userId),
    });

    return { user };
  },
  component: UserPage,
});

// Create router with query client
export const router = createRouter({
  routeTree,
  // Make the React Query context available in loaders
  context: {
    queryClient,
  },
});
```

## Best Practices

1. **Organization**: Keep your route definitions organized, either in a central file for smaller apps or in a file-based structure for larger apps.

2. **Type Safety**: Take advantage of TypeScript for type-safe routes and parameters.

3. **Loaders**: Use loaders to fetch data before a route renders, improving user experience.

4. **Error Handling**: Always provide error components to handle route-specific errors.

5. **Guards**: Implement authentication and authorization checks to protect routes.

6. **Search Parameters**: Use search parameters for filtering, sorting, and pagination.

7. **Route Splitting**: Split your routes into logical groups for better organization and code-splitting.

TanStack Router is a powerful tool for building modern React applications with robust routing capabilities. Start with the basics and gradually explore its more advanced features as you become comfortable with it.
