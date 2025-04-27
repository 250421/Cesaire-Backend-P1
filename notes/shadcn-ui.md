# ShadCN UI Components Guide (with TypeScript)

## What is ShadCN UI?

ShadCN UI is a collection of reusable components built using Radix UI and Tailwind CSS. Unlike traditional component libraries, ShadCN UI is not installed as a package. Instead, you copy and paste the components you need into your project.

Key benefits:

- High-quality, accessible components
- Customizable (since you own the code)
- Styled with Tailwind CSS
- Fully typed with TypeScript
- Works well with modern React and Next.js

## Setting Up ShadCN UI

First, you need to set up a new project with the necessary dependencies:

```bash
# For a new project with Vite (React + TypeScript)
npm create vite@latest my-app -- --template react-ts
cd my-app

# Install necessary dependencies
npm install

# Add ShadCN UI CLI
npm install -D @shadcn/ui

# Initialize ShadCN UI
npx shadcn-ui@latest init
```

During initialization, you'll be asked several questions:

1. Would you like to use TypeScript? **Yes**
2. Which style would you like to use? **Default**
3. Which color would you like to use as base color? **Slate**
4. Where is your global CSS file? **src/index.css**
5. Where is your tailwind.config.js file? **tailwind.config.js**
6. Configure the import alias for components? **@/components**
7. Configure the import alias for utilities? **@/lib/utils**

## Adding Components

After initialization, you can add components one by one:

```bash
# Add button component
npx shadcn-ui@latest add button

# Add input, form components
npx shadcn-ui@latest add input form

# Add dialog (modal) component
npx shadcn-ui@latest add dialog
```

## Basic Components Usage

### Button Component

```tsx
import { Button } from "@/components/ui/button";

function MyComponent() {
  return (
    <div className="space-y-4">
      <h2>Button Variants</h2>

      {/* Default button */}
      <Button>Default Button</Button>

      {/* Button variants */}
      <Button variant="destructive">Destructive</Button>
      <Button variant="outline">Outline</Button>
      <Button variant="secondary">Secondary</Button>
      <Button variant="ghost">Ghost</Button>
      <Button variant="link">Link</Button>

      {/* Button sizes */}
      <div className="flex items-center gap-2">
        <Button size="sm">Small</Button>
        <Button>Default</Button>
        <Button size="lg">Large</Button>
      </div>

      {/* With icon */}
      <Button>
        <svg
          className="mr-2 h-4 w-4"
          fill="none"
          viewBox="0 0 24 24"
          stroke="currentColor"
        >
          <path
            strokeLinecap="round"
            strokeLinejoin="round"
            strokeWidth={2}
            d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-8l-4-4m0 0L8 8m4-4v12"
          />
        </svg>
        With Icon
      </Button>

      {/* Disabled button */}
      <Button disabled>Disabled</Button>
    </div>
  );
}
```

### Input Component

```tsx
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { useState } from "react";

function InputExample() {
  const [value, setValue] = useState("");

  return (
    <div className="space-y-4 w-80">
      <div className="space-y-2">
        <Label htmlFor="email">Email</Label>
        <Input
          id="email"
          type="email"
          placeholder="Enter your email"
          value={value}
          onChange={(e) => setValue(e.target.value)}
        />
      </div>

      {/* Disabled input */}
      <Input disabled placeholder="Disabled input" />

      {/* With icon */}
      <div className="relative">
        <svg
          className="absolute left-2 top-2.5 h-4 w-4 text-gray-500"
          fill="none"
          viewBox="0 0 24 24"
          stroke="currentColor"
        >
          <path
            strokeLinecap="round"
            strokeLinejoin="round"
            strokeWidth={2}
            d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"
          />
        </svg>
        <Input placeholder="Search..." className="pl-8" />
      </div>
    </div>
  );
}
```

## Forms with ShadCN UI and React Hook Form

ShadCN UI works well with React Hook Form and Zod for validation:

```tsx
import { zodResolver } from "@hookform/resolvers/zod";
import { useForm } from "react-hook-form";
import * as z from "zod";
import { Button } from "@/components/ui/button";
import {
  Form,
  FormControl,
  FormDescription,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form";
import { Input } from "@/components/ui/input";

// Define form validation schema with Zod
const formSchema = z.object({
  username: z
    .string()
    .min(3, "Username must be at least 3 characters")
    .max(20, "Username cannot be more than 20 characters"),
  email: z.string().email("Please enter a valid email address"),
});

// Define the form values type using Zod schema
type FormValues = z.infer<typeof formSchema>;

function SignupForm() {
  // Initialize form with React Hook Form and Zod validation
  const form = useForm<FormValues>({
    resolver: zodResolver(formSchema),
    defaultValues: {
      username: "",
      email: "",
    },
  });

  // Handle form submission
  function onSubmit(values: FormValues) {
    console.log(values);
    // Handle submission logic here
    alert(`Form submitted with: ${JSON.stringify(values, null, 2)}`);
  }

  return (
    <div className="max-w-md mx-auto p-6 bg-white rounded-lg shadow">
      <h2 className="text-2xl font-bold mb-6">Sign Up</h2>

      <Form {...form}>
        <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-6">
          {/* Username field */}
          <FormField
            control={form.control}
            name="username"
            render={({ field }) => (
              <FormItem>
                <FormLabel>Username</FormLabel>
                <FormControl>
                  <Input placeholder="johndoe" {...field} />
                </FormControl>
                <FormDescription>
                  This will be your public display name.
                </FormDescription>
                <FormMessage />
              </FormItem>
            )}
          />

          {/* Email field */}
          <FormField
            control={form.control}
            name="email"
            render={({ field }) => (
              <FormItem>
                <FormLabel>Email</FormLabel>
                <FormControl>
                  <Input
                    type="email"
                    placeholder="john@example.com"
                    {...field}
                  />
                </FormControl>
                <FormDescription>
                  We'll never share your email with anyone else.
                </FormDescription>
                <FormMessage />
              </FormItem>
            )}
          />

          <Button type="submit">Sign Up</Button>
        </form>
      </Form>
    </div>
  );
}

export default SignupForm;
```

## Dialog Component (Modal)

```tsx
import { useState } from "react";
import { Button } from "@/components/ui/button";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";

function DialogExample() {
  const [open, setOpen] = useState(false);
  const [name, setName] = useState("");

  const handleSave = () => {
    alert(`Name saved: ${name}`);
    setOpen(false);
  };

  return (
    <Dialog open={open} onOpenChange={setOpen}>
      <DialogTrigger asChild>
        <Button>Edit Profile</Button>
      </DialogTrigger>
      <DialogContent className="sm:max-w-[425px]">
        <DialogHeader>
          <DialogTitle>Edit profile</DialogTitle>
          <DialogDescription>
            Make changes to your profile here. Click save when you're done.
          </DialogDescription>
        </DialogHeader>
        <div className="grid gap-4 py-4">
          <div className="grid grid-cols-4 items-center gap-4">
            <Label htmlFor="name" className="text-right">
              Name
            </Label>
            <Input
              id="name"
              value={name}
              onChange={(e) => setName(e.target.value)}
              className="col-span-3"
            />
          </div>
        </div>
        <DialogFooter>
          <Button type="submit" onClick={handleSave}>
            Save changes
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}
```

## Card Component

```tsx
import {
  Card,
  CardContent,
  CardDescription,
  CardFooter,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { Button } from "@/components/ui/button";

interface Profile {
  name: string;
  email: string;
  role: string;
}

function ProfileCard({ profile }: { profile: Profile }) {
  return (
    <Card className="w-[350px]">
      <CardHeader>
        <CardTitle>{profile.name}</CardTitle>
        <CardDescription>{profile.role}</CardDescription>
      </CardHeader>
      <CardContent>
        <div className="space-y-2">
          <div className="text-sm">
            <span className="font-medium">Email: </span>
            {profile.email}
          </div>
        </div>
      </CardContent>
      <CardFooter className="flex justify-between">
        <Button variant="outline">Cancel</Button>
        <Button>Edit Profile</Button>
      </CardFooter>
    </Card>
  );
}

// Usage
function CardExample() {
  const profile = {
    name: "John Doe",
    email: "john@example.com",
    role: "Software Developer",
  };

  return (
    <div className="p-4">
      <h2 className="text-2xl font-bold mb-4">User Profile</h2>
      <ProfileCard profile={profile} />
    </div>
  );
}
```

## Customizing Components

ShadCN components can be customized in several ways:

### 1. Using Tailwind Classes

```tsx
// Add custom Tailwind classes
<Button className="bg-gradient-to-r from-purple-500 to-pink-500 hover:from-purple-600 hover:to-pink-600">
  Gradient Button
</Button>
```

### 2. Modifying Component Source Code

Since you own the component code, you can modify it directly:

```tsx
// In your components/ui/button.tsx file
import { cva } from "class-variance-authority";

export const buttonVariants = cva(
  "rounded-md text-sm font-medium transition-colors focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:opacity-50 disabled:pointer-events-none ring-offset-background",
  {
    variants: {
      variant: {
        default: "bg-primary text-primary-foreground hover:bg-primary/90",
        destructive:
          "bg-destructive text-destructive-foreground hover:bg-destructive/90",
        outline:
          "border border-input hover:bg-accent hover:text-accent-foreground",
        secondary:
          "bg-secondary text-secondary-foreground hover:bg-secondary/80",
        ghost: "hover:bg-accent hover:text-accent-foreground",
        link: "underline-offset-4 hover:underline text-primary",
        // Add a custom gradient variant
        gradient:
          "bg-gradient-to-r from-purple-500 to-pink-500 text-white hover:from-purple-600 hover:to-pink-600",
      },
      size: {
        default: "h-10 py-2 px-4",
        sm: "h-9 px-3 rounded-md",
        lg: "h-11 px-8 rounded-md",
        // Add an extra-large size
        xl: "h-14 px-10 rounded-md text-base",
      },
    },
    defaultVariants: {
      variant: "default",
      size: "default",
    },
  }
);
```

### 3. Creating Component Compositions

```tsx
import { Button } from "@/components/ui/button";
import { IconProps } from "@/types/icon"; // Define your own icon types

interface ActionButtonProps {
  children: React.ReactNode;
  onClick: () => void;
  icon?: React.ComponentType<IconProps>;
  variant?:
    | "default"
    | "destructive"
    | "outline"
    | "secondary"
    | "ghost"
    | "link";
  loading?: boolean;
  disabled?: boolean;
}

function ActionButton({
  children,
  onClick,
  icon: Icon,
  variant = "default",
  loading = false,
  disabled = false,
}: ActionButtonProps) {
  return (
    <Button
      variant={variant}
      onClick={onClick}
      disabled={disabled || loading}
      className="flex items-center gap-2"
    >
      {loading ? (
        <svg
          className="animate-spin -ml-1 mr-2 h-4 w-4 text-current"
          xmlns="http://www.w3.org/2000/svg"
          fill="none"
          viewBox="0 0 24 24"
        >
          <circle
            className="opacity-25"
            cx="12"
            cy="12"
            r="10"
            stroke="currentColor"
            strokeWidth="4"
          ></circle>
          <path
            className="opacity-75"
            fill="currentColor"
            d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"
          ></path>
        </svg>
      ) : Icon ? (
        <Icon className="h-4 w-4" />
      ) : null}
      {children}
    </Button>
  );
}
```

## Best Practices

1. **Choose Components Wisely**: Only add the components you need to keep your bundle size small.

2. **Consistent Styling**: When customizing components, maintain a consistent look and feel throughout your application.

3. **Composition**: Combine ShadCN components to build more complex UIs.

4. **Type Safety**: Leverage TypeScript to ensure your components are used correctly.

5. **Accessibility**: ShadCN components are accessible by default, but ensure your customizations maintain this accessibility.

6. **Dark Mode**: Use Tailwind's dark mode with ShadCN for a complete dark mode experience:

```tsx
// In your tailwind.config.js
/** @type {import('tailwindcss').Config} */
module.exports = {
  darkMode: ["class"],
  // other config...
};

// Toggle dark mode
import { useState, useEffect } from "react";
import { Button } from "@/components/ui/button";
import { Moon, Sun } from "lucide-react"; // Using lucide-react icons

function ThemeToggle() {
  const [isDark, setIsDark] = useState(false);

  useEffect(() => {
    const isDarkMode =
      localStorage.theme === "dark" ||
      (!("theme" in localStorage) &&
        window.matchMedia("(prefers-color-scheme: dark)").matches);

    setIsDark(isDarkMode);

    if (isDarkMode) {
      document.documentElement.classList.add("dark");
    } else {
      document.documentElement.classList.remove("dark");
    }
  }, []);

  const toggleTheme = () => {
    const newTheme = !isDark;
    setIsDark(newTheme);

    if (newTheme) {
      document.documentElement.classList.add("dark");
      localStorage.theme = "dark";
    } else {
      document.documentElement.classList.remove("dark");
      localStorage.theme = "light";
    }
  };

  return (
    <Button
      variant="ghost"
      size="icon"
      onClick={toggleTheme}
      aria-label={isDark ? "Switch to light mode" : "Switch to dark mode"}
    >
      {isDark ? <Sun className="h-5 w-5" /> : <Moon className="h-5 w-5" />}
    </Button>
  );
}
```

Remember that ShadCN UI gives you the flexibility to customize components to fit your specific needs. Use this to your advantage but try to maintain consistency across your application.
