# TanStack Query Basics (with TypeScript and axios)

## What is TanStack Query?

TanStack Query (formerly React Query) is a powerful data-fetching library for React that simplifies:

- Fetching data from APIs
- Caching the results
- Updating the data
- Error handling
- Loading states

It eliminates much of the boilerplate code typically needed when working with data in React.

## Setting Up

First, install the necessary packages:

```bash
# Install TanStack Query and axios
npm install @tanstack/react-query axios
```

Then, set up the QueryClient in your main app file:

```tsx
// src/main.tsx or App.tsx
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { ReactQueryDevtools } from "@tanstack/react-query-devtools";
import React from "react";
import ReactDOM from "react-dom/client";
import App from "./App";

// Create a client
const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      staleTime: 1000 * 60 * 5, // 5 minutes
      refetchOnWindowFocus: false,
    },
  },
});

ReactDOM.createRoot(document.getElementById("root")!).render(
  <React.StrictMode>
    <QueryClientProvider client={queryClient}>
      <App />
      {/* Optional: adds development UI for debugging */}
      <ReactQueryDevtools initialIsOpen={false} />
    </QueryClientProvider>
  </React.StrictMode>
);
```

## Basic Queries

The most common use case is fetching data from an API:

```tsx
// src/components/UserList.tsx
import { useQuery } from "@tanstack/react-query";
import axios from "axios";

// Define the type for our data
interface User {
  id: number;
  name: string;
  email: string;
}

// API function using axios
const fetchUsers = async (): Promise<User[]> => {
  const response = await axios.get(
    "https://jsonplaceholder.typicode.com/users"
  );
  return response.data;
};

function UserList() {
  // Use the useQuery hook to fetch data
  const { data, isLoading, isError, error } = useQuery({
    queryKey: ["users"], // Unique identifier for this query
    queryFn: fetchUsers, // Function that returns a promise
  });

  // Handle different states
  if (isLoading) return <div>Loading users...</div>;
  if (isError) return <div>Error: {error.message}</div>;
  if (!data) return <div>No users found</div>;

  // Render the data
  return (
    <div>
      <h2>Users</h2>
      <ul>
        {data.map((user) => (
          <li key={user.id}>
            {user.name} - {user.email}
          </li>
        ))}
      </ul>
    </div>
  );
}

export default UserList;
```

## Queries with Parameters

Often you'll need to fetch data based on parameters:

```tsx
// src/components/UserProfile.tsx
import { useQuery } from "@tanstack/react-query";
import axios from "axios";

interface User {
  id: number;
  name: string;
  email: string;
  phone: string;
  website: string;
}

// API function with parameter
const fetchUser = async (userId: number): Promise<User> => {
  const response = await axios.get(
    `https://jsonplaceholder.typicode.com/users/${userId}`
  );
  return response.data;
};

interface UserProfileProps {
  userId: number;
}

function UserProfile({ userId }: UserProfileProps) {
  const { data, isLoading, isError, error } = useQuery({
    queryKey: ["user", userId], // Include parameters in the queryKey
    queryFn: () => fetchUser(userId),
    enabled: !!userId, // Only run the query if userId is truthy
  });

  if (isLoading) return <div>Loading profile...</div>;
  if (isError) return <div>Error: {error.message}</div>;
  if (!data) return <div>No user found</div>;

  return (
    <div>
      <h2>{data.name}</h2>
      <p>Email: {data.email}</p>
      <p>Phone: {data.phone}</p>
      <p>Website: {data.website}</p>
    </div>
  );
}

export default UserProfile;
```

## Mutations (Creating, Updating, Deleting Data)

Mutations are used for changing data:

```tsx
// src/components/CreateUser.tsx
import { useMutation, useQueryClient } from "@tanstack/react-query";
import axios from "axios";
import { useState, FormEvent } from "react";

interface User {
  id: number;
  name: string;
  email: string;
}

interface UserInput {
  name: string;
  email: string;
}

// API function for creating a user
const createUser = async (userData: UserInput): Promise<User> => {
  const response = await axios.post(
    "https://jsonplaceholder.typicode.com/users",
    userData
  );
  return response.data;
};

function CreateUser() {
  // State for form inputs
  const [name, setName] = useState("");
  const [email, setEmail] = useState("");

  // Get QueryClient from context
  const queryClient = useQueryClient();

  // Create mutation
  const mutation = useMutation({
    mutationFn: createUser,
    // When mutation succeeds, invalidate the users query to refresh the list
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["users"] });
      // Reset form
      setName("");
      setEmail("");
      alert("User created successfully!");
    },
  });

  const handleSubmit = (e: FormEvent) => {
    e.preventDefault();
    // Validate form
    if (!name || !email) {
      alert("Please fill in all fields");
      return;
    }

    // Submit the form data
    mutation.mutate({ name, email });
  };

  return (
    <div>
      <h2>Create New User</h2>
      <form onSubmit={handleSubmit}>
        <div>
          <label htmlFor="name">Name:</label>
          <input
            id="name"
            type="text"
            value={name}
            onChange={(e) => setName(e.target.value)}
          />
        </div>
        <div>
          <label htmlFor="email">Email:</label>
          <input
            id="email"
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
          />
        </div>
        <button type="submit" disabled={mutation.isPending}>
          {mutation.isPending ? "Creating..." : "Create User"}
        </button>
      </form>

      {/* Show error if mutation fails */}
      {mutation.isError && (
        <div style={{ color: "red" }}>Error: {mutation.error.message}</div>
      )}
    </div>
  );
}

export default CreateUser;
```

## Error Handling

TanStack Query provides built-in error handling:

```tsx
import { useQuery } from "@tanstack/react-query";
import axios from "axios";

const fetchData = async () => {
  try {
    const response = await axios.get("/api/some-endpoint");
    return response.data;
  } catch (error) {
    // Handle Axios errors specifically
    if (axios.isAxiosError(error)) {
      // Access axios-specific error info
      const errorMessage =
        error.response?.data?.message || "An unknown error occurred";
      throw new Error(errorMessage);
    }
    // Re-throw other errors
    throw error;
  }
};

function DataComponent() {
  const { data, isLoading, isError, error } = useQuery({
    queryKey: ["data"],
    queryFn: fetchData,
    // Additional error handling options
    retry: 3, // Retry failed requests 3 times
    retryDelay: (attemptIndex) => Math.min(1000 * 2 ** attemptIndex, 30000), // Exponential backoff
  });

  if (isLoading) return <div>Loading...</div>;
  if (isError) {
    // You can handle different types of errors differently
    return (
      <div className="error-container">
        <h3>Something went wrong</h3>
        <p>{error.message}</p>
        <button onClick={() => window.location.reload()}>Retry</button>
      </div>
    );
  }

  return <div>{/* Render your data */}</div>;
}
```

## Custom Query Hooks

To keep your code organized, create custom hooks for specific API calls:

```tsx
// src/hooks/useUsers.ts
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import axios from "axios";

interface User {
  id: number;
  name: string;
  email: string;
}

// API functions
const api = {
  getUsers: async (): Promise<User[]> => {
    const response = await axios.get("/api/users");
    return response.data;
  },

  getUser: async (id: number): Promise<User> => {
    const response = await axios.get(`/api/users/${id}`);
    return response.data;
  },

  createUser: async (user: Omit<User, "id">): Promise<User> => {
    const response = await axios.post("/api/users", user);
    return response.data;
  },

  updateUser: async ({ id, ...data }: User): Promise<User> => {
    const response = await axios.put(`/api/users/${id}`, data);
    return response.data;
  },

  deleteUser: async (id: number): Promise<void> => {
    await axios.delete(`/api/users/${id}`);
  },
};

// Custom hooks
export function useUsers() {
  return useQuery({
    queryKey: ["users"],
    queryFn: api.getUsers,
  });
}

export function useUser(id: number) {
  return useQuery({
    queryKey: ["users", id],
    queryFn: () => api.getUser(id),
    enabled: !!id,
  });
}

export function useCreateUser() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: api.createUser,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["users"] });
    },
  });
}

export function useUpdateUser() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: api.updateUser,
    onSuccess: (data) => {
      queryClient.invalidateQueries({ queryKey: ["users"] });
      queryClient.invalidateQueries({ queryKey: ["users", data.id] });
    },
  });
}

export function useDeleteUser() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: api.deleteUser,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["users"] });
    },
  });
}
```

Using the custom hooks in components:

```tsx
// src/components/UserManager.tsx
import { useState } from "react";
import {
  useUsers,
  useUser,
  useCreateUser,
  useUpdateUser,
  useDeleteUser,
} from "../hooks/useUsers";

function UserManager() {
  const [selectedUserId, setSelectedUserId] = useState<number | null>(null);

  // Use our custom hooks
  const { data: users, isLoading: isLoadingUsers } = useUsers();
  const { data: selectedUser } = useUser(selectedUserId || 0);
  const createUserMutation = useCreateUser();
  const updateUserMutation = useUpdateUser();
  const deleteUserMutation = useDeleteUser();

  // Component JSX and event handlers...
  // (Implementation details omitted for brevity)

  return <div>{/* Your component UI */}</div>;
}
```

## Tips and Best Practices

1. **QueryKeys**: Use consistent patterns for query keys. Include all variables that the query depends on.

2. **Prefetching**: Prefetch data for a better user experience:

   ```tsx
   // Prefetch user data when hovering over a link
   const queryClient = useQueryClient();

   const prefetchUser = (userId: number) => {
     queryClient.prefetchQuery({
       queryKey: ["users", userId],
       queryFn: () => api.getUser(userId),
     });
   };

   // In your component
   <button
     onMouseEnter={() => prefetchUser(user.id)}
     onClick={() => setSelectedUserId(user.id)}
   >
     View {user.name}
   </button>;
   ```

3. **Cache invalidation**: Be strategic about when to invalidate queries:

   ```tsx
   // After creating a comment, invalidate both comments and the related post
   queryClient.invalidateQueries({ queryKey: ["comments"] });
   queryClient.invalidateQueries({ queryKey: ["posts", postId] });
   ```

4. **Background refreshing**: TanStack Query can refresh data in the background:

   ```tsx
   const { data } = useQuery({
     queryKey: ["users"],
     queryFn: fetchUsers,
     refetchInterval: 60000, // Refetch every minute
   });
   ```

5. **Optimistic updates**: Update the UI before the server responds for a smoother experience:

   ```tsx
   const queryClient = useQueryClient();

   const mutation = useMutation({
     mutationFn: updateTodo,
     // Optimistically update the todo
     onMutate: async (newTodo) => {
       // Cancel any outgoing refetches
       await queryClient.cancelQueries({ queryKey: ["todos", newTodo.id] });

       // Snapshot the previous value
       const previousTodo = queryClient.getQueryData(["todos", newTodo.id]);

       // Optimistically update to the new value
       queryClient.setQueryData(["todos", newTodo.id], newTodo);

       // Return a context object with the snapshot
       return { previousTodo };
     },
     // If the mutation fails, use the context returned from onMutate to roll back
     onError: (err, newTodo, context) => {
       queryClient.setQueryData(["todos", newTodo.id], context?.previousTodo);
     },
   });
   ```

Remember that TanStack Query is a powerful tool that simplifies complex data fetching patterns. Start with the basics and gradually explore its more advanced features as you become comfortable with it.
