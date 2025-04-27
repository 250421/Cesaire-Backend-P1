# React Fundamentals for Beginners (with TypeScript)

## What is React?

React is a JavaScript library for building user interfaces. It allows you to create reusable UI components that efficiently update when your data changes.

Key characteristics of React:

- **Component-Based**: Build encapsulated components that manage their own state
- **Declarative**: Tell React what you want to display, and it handles the DOM updates
- **Learn Once, Write Anywhere**: Works with different technologies and platforms

## Setting Up a React Project with TypeScript

Using Vite (recommended):

```bash
# Create a new React project with TypeScript
npm create vite@latest my-app -- --template react-ts

# Navigate to project directory
cd my-app

# Install dependencies
npm install

# Start development server
npm run dev
```

## Understanding Components

Components are the building blocks of a React application. They are reusable pieces of code that return React elements.

### Function Components (Preferred)

```tsx
// Simple function component
function Greeting() {
  return <h1>Hello, World!</h1>;
}

// With TypeScript props
type GreetingProps = {
  name: string;
  age?: number; // Optional prop
};

function Greeting({ name, age }: GreetingProps) {
  return (
    <div>
      <h1>Hello, {name}!</h1>
      {age !== undefined && <p>You are {age} years old.</p>}
    </div>
  );
}

// Usage
<Greeting name="John" age={25} />;
```

## JSX

JSX is a syntax extension for JavaScript that looks similar to HTML but is actually JavaScript.

```tsx
// JSX example
const element = <h1>Hello, world!</h1>;

// JSX with expressions
const name = "John";
const element = <h1>Hello, {name}!</h1>;

// JSX with attributes (use camelCase)
const element = <img src={userPhoto} alt="User" className="profile-pic" />;

// JSX children
const element = (
  <div>
    <h1>Hello!</h1>
    <p>Welcome to React.</p>
  </div>
);
```

## Props

Props are inputs to components. They are passed from parent to child component.

```tsx
// Define props interface with TypeScript
interface ButtonProps {
  label: string;
  onClick: () => void;
  color?: "primary" | "secondary" | "danger"; // Union type for specific values
}

// Component with props
function Button({ label, onClick, color = "primary" }: ButtonProps) {
  return (
    <button onClick={onClick} className={`btn btn-${color}`}>
      {label}
    </button>
  );
}

// Usage
function App() {
  const handleClick = () => {
    alert("Button clicked!");
  };

  return (
    <div>
      <Button label="Click Me" onClick={handleClick} color="primary" />
    </div>
  );
}
```

## State

State is data that changes over time in a component. When state changes, React re-renders the component.

```tsx
import { useState } from "react";

function Counter() {
  // Initialize state with useState hook
  const [count, setCount] = useState<number>(0);

  return (
    <div>
      <p>Count: {count}</p>
      <button onClick={() => setCount(count + 1)}>Increment</button>
      <button onClick={() => setCount(count - 1)}>Decrement</button>
    </div>
  );
}
```

### More Complex State Example

```tsx
import { useState } from "react";

// Define the shape of our user object with TypeScript
interface User {
  name: string;
  email: string;
}

function UserProfile() {
  // Initialize with a typed state
  const [user, setUser] = useState<User>({
    name: "",
    email: "",
  });

  const handleNameChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setUser({
      ...user, // Keep existing user data
      name: e.target.value, // Update only the name
    });
  };

  const handleEmailChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setUser({
      ...user, // Keep existing user data
      email: e.target.value, // Update only the email
    });
  };

  return (
    <div>
      <h2>User Profile</h2>
      <div>
        <label>
          Name:
          <input type="text" value={user.name} onChange={handleNameChange} />
        </label>
      </div>
      <div>
        <label>
          Email:
          <input type="email" value={user.email} onChange={handleEmailChange} />
        </label>
      </div>
      <div>
        <h3>Preview:</h3>
        <p>Name: {user.name || "(Not provided)"}</p>
        <p>Email: {user.email || "(Not provided)"}</p>
      </div>
    </div>
  );
}
```

## Effects

Effects let you run code after render, useful for data fetching, subscriptions, or DOM manipulations.

```tsx
import { useState, useEffect } from "react";

interface User {
  id: number;
  name: string;
  email: string;
}

function UserProfile({ userId }: { userId: number }) {
  const [user, setUser] = useState<User | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    // This function runs after component renders
    const fetchUser = async () => {
      try {
        setLoading(true);
        const response = await fetch(`https://api.example.com/users/${userId}`);

        if (!response.ok) {
          throw new Error("Failed to fetch user data");
        }

        const userData = await response.json();
        setUser(userData);
        setError(null);
      } catch (err) {
        setError("Error fetching user data");
        setUser(null);
      } finally {
        setLoading(false);
      }
    };

    fetchUser();

    // Cleanup function (optional)
    return () => {
      // Cleanup code (e.g., cancel requests, remove event listeners)
      console.log("Component unmounted or userId changed");
    };
  }, [userId]); // Dependency array - effect runs when userId changes

  if (loading) return <div>Loading...</div>;
  if (error) return <div>Error: {error}</div>;
  if (!user) return <div>No user found</div>;

  return (
    <div>
      <h2>User Profile</h2>
      <p>Name: {user.name}</p>
      <p>Email: {user.email}</p>
    </div>
  );
}
```

## Lists and Keys

When rendering lists in React, you should provide a unique "key" for each item.

```tsx
interface TodoItem {
  id: number;
  text: string;
  completed: boolean;
}

function TodoList({ items }: { items: TodoItem[] }) {
  return (
    <ul>
      {items.map((item) => (
        // Key helps React identify which items have changed
        <li key={item.id}>
          <span
            style={{ textDecoration: item.completed ? "line-through" : "none" }}
          >
            {item.text}
          </span>
        </li>
      ))}
    </ul>
  );
}

// Usage
const todos: TodoItem[] = [
  { id: 1, text: "Learn React", completed: true },
  { id: 2, text: "Learn TypeScript", completed: false },
  { id: 3, text: "Build an app", completed: false },
];

<TodoList items={todos} />;
```

## Forms

Handling forms in React typically uses controlled components—form elements controlled by React state.

```tsx
import { useState, FormEvent } from "react";

function LoginForm() {
  const [email, setEmail] = useState<string>("");
  const [password, setPassword] = useState<string>("");
  const [error, setError] = useState<string | null>(null);

  const handleSubmit = (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault(); // Prevent default form submission

    // Form validation
    if (!email || !password) {
      setError("Please fill in all fields");
      return;
    }

    // Clear any previous errors
    setError(null);

    console.log("Form submitted:", { email, password });
    // Here you would typically call an API to authenticate the user
  };

  return (
    <form onSubmit={handleSubmit}>
      <h2>Login</h2>

      {error && <div className="error">{error}</div>}

      <div>
        <label htmlFor="email">Email:</label>
        <input
          id="email"
          type="email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          required
        />
      </div>

      <div>
        <label htmlFor="password">Password:</label>
        <input
          id="password"
          type="password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          required
        />
      </div>

      <button type="submit">Login</button>
    </form>
  );
}
```

## Conditional Rendering

Render components conditionally based on state or props.

```tsx
interface UserProfileProps {
  isLoggedIn: boolean;
  username?: string;
}

function UserProfile({ isLoggedIn, username }: UserProfileProps) {
  // Using if/else
  if (!isLoggedIn) {
    return <div>Please log in to view your profile</div>;
  }

  // Since we're logged in here, username should exist
  return <div>Welcome, {username}</div>;
}

// Alternative approaches:

function UserInfo({ isLoggedIn, username }: UserProfileProps) {
  return (
    <div>
      {/* Ternary operator */}
      {isLoggedIn ? <h2>Welcome, {username}</h2> : <button>Log In</button>}

      {/* Logical AND operator - renders only if isLoggedIn is true */}
      {isLoggedIn && <p>You are now logged in as {username}</p>}
    </div>
  );
}
```

## Handling Events

React events are named using camelCase and pass functions as event handlers.

```tsx
import { useState } from "react";

function ToggleButton() {
  const [isOn, setIsOn] = useState<boolean>(false);

  // Event handler function
  const handleClick = () => {
    setIsOn(!isOn);
  };

  return (
    <button onClick={handleClick} className={isOn ? "btn-on" : "btn-off"}>
      {isOn ? "ON" : "OFF"}
    </button>
  );
}

// With event parameters
function FormInput() {
  const [text, setText] = useState<string>("");

  // Using the event parameter with TypeScript
  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setText(e.target.value);
  };

  return (
    <div>
      <input
        type="text"
        value={text}
        onChange={handleChange}
        placeholder="Type something..."
      />
      <p>You typed: {text}</p>
    </div>
  );
}
```

## Next Steps

After understanding these basics, you can explore:

1. React Router for navigation
2. Context API for state management
3. Custom Hooks for reusable logic
4. ShadCN for UI components
5. TanStack Query for data fetching
6. TanStack Router for routing

Remember: Practice is key! Try building small components and gradually increase complexity.
