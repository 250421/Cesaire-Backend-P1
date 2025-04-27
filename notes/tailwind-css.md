# TailwindCSS Basics

## What is TailwindCSS?

TailwindCSS is a utility-first CSS framework that allows you to build designs directly in your markup by composing utility classes. Unlike traditional CSS frameworks like Bootstrap that provide pre-designed components, Tailwind gives you low-level utility classes that let you build completely custom designs.

Key benefits:

- No need to write custom CSS for most designs
- Consistent design system with built-in constraints
- Responsive design made easy
- Dark mode support
- Highly customizable

## Setting Up TailwindCSS

Here's how to add Tailwind to a React + TypeScript project:

```bash
# Install Tailwind and its dependencies
npm install -D tailwindcss postcss autoprefixer

# Generate configuration files
npx tailwindcss init -p
```

Configure your `tailwind.config.js` file:

```js
/** @type {import('tailwindcss').Config} */
module.exports = {
  content: ["./index.html", "./src/**/*.{js,ts,jsx,tsx}"],
  theme: {
    extend: {},
  },
  plugins: [],
};
```

Add Tailwind directives to your CSS in `src/index.css`:

```css
@tailwind base;
@tailwind components;
@tailwind utilities;
```

## Core Concepts

### Utility Classes

Tailwind provides utility classes for almost everything:

```tsx
// Example: Button with utility classes
function PrimaryButton({ children }: { children: React.ReactNode }) {
  return (
    <button className="bg-blue-500 hover:bg-blue-600 text-white font-semibold py-2 px-4 rounded shadow">
      {children}
    </button>
  );
}
```

### Common Utility Categories

#### Spacing

Padding and margin utilities follow a consistent pattern:

- `p-{size}` for padding on all sides
- `px-{size}` for horizontal padding (left and right)
- `py-{size}` for vertical padding (top and bottom)
- `pt-{size}`, `pr-{size}`, `pb-{size}`, `pl-{size}` for specific sides

```tsx
<div className="p-4">All sides padding</div>
<div className="px-4 py-2">Horizontal and vertical padding</div>
<div className="pt-2 pb-4">Top and bottom padding</div>
<div className="m-4">All sides margin</div>
<div className="mx-auto">Center horizontally with auto margins</div>
```

#### Typography

```tsx
<h1 className="text-2xl font-bold text-gray-800">Heading</h1>
<p className="text-base text-gray-600">Normal paragraph text</p>
<p className="text-sm text-gray-500">Smaller text</p>
<p className="font-semibold">Semibold text</p>
<p className="italic">Italic text</p>
<p className="underline">Underlined text</p>
<p className="text-center">Centered text</p>
```

#### Colors

Tailwind includes a default color palette with various shades:

```tsx
<div className="text-blue-500">Blue text</div>
<div className="bg-green-200">Light green background</div>
<div className="border border-red-300">Red border</div>
```

Each color has shades from 50 to 900, where lower numbers are lighter.

#### Sizing

```tsx
<div className="w-full">Full width</div>
<div className="w-1/2">Half width</div>
<div className="w-64">Fixed width (16rem or 256px)</div>
<div className="h-screen">Full viewport height</div>
<div className="max-w-md">Maximum width (28rem)</div>
```

#### Flexbox

```tsx
<div className="flex">
  <div>Item 1</div>
  <div>Item 2</div>
</div>

<div className="flex flex-col">
  <div>Item 1</div>
  <div>Item 2</div>
</div>

<div className="flex justify-between items-center">
  <div>Left</div>
  <div>Right</div>
</div>

<div className="flex gap-4">
  <div>Item with gap</div>
  <div>Item with gap</div>
</div>
```

#### Grid

```tsx
<div className="grid grid-cols-3 gap-4">
  <div>Grid item 1</div>
  <div>Grid item 2</div>
  <div>Grid item 3</div>
  <div>Grid item 4</div>
</div>

<div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
  {/* Responsive grid */}
</div>
```

## Responsive Design

Tailwind makes responsive design easy with breakpoint prefixes:

- `sm:` - Small screens (640px and up)
- `md:` - Medium screens (768px and up)
- `lg:` - Large screens (1024px and up)
- `xl:` - Extra large screens (1280px and up)
- `2xl:` - 2X large screens (1536px and up)

```tsx
<div className="text-center md:text-left">
  {/* Centered on mobile, left-aligned on medium screens and up */}
</div>

<div className="flex flex-col md:flex-row">
  {/* Stacked on mobile, side-by-side on medium screens and up */}
</div>

<div className="hidden md:block">
  {/* Hidden on mobile, visible on medium screens and up */}
</div>
```

## Component Example

Here's how to build a card component with Tailwind:

```tsx
interface CardProps {
  title: string;
  description: string;
  imageUrl?: string;
}

function Card({ title, description, imageUrl }: CardProps) {
  return (
    <div className="bg-white rounded-lg shadow-md overflow-hidden">
      {imageUrl && (
        <img src={imageUrl} alt={title} className="w-full h-48 object-cover" />
      )}
      <div className="p-4">
        <h3 className="text-lg font-semibold text-gray-800">{title}</h3>
        <p className="mt-2 text-gray-600">{description}</p>
        <button className="mt-4 bg-blue-500 hover:bg-blue-600 text-white font-medium py-2 px-4 rounded transition-colors">
          Read More
        </button>
      </div>
    </div>
  );
}
```

## Hover, Focus, and Other States

Tailwind provides modifiers for various states:

```tsx
<button className="bg-blue-500 hover:bg-blue-600 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-opacity-50">
  Hover and Focus Effects
</button>

<input
  type="text"
  className="border border-gray-300 rounded px-4 py-2 focus:border-blue-500 focus:ring focus:ring-blue-200 focus:ring-opacity-50"
  placeholder="Focus styling"
/>

<button className="bg-green-500 hover:bg-green-600 active:bg-green-700">
  Active State
</button>

<div className="group">
  <button className="bg-purple-500 group-hover:bg-purple-600">
    Change on parent hover
  </button>
</div>
```

## Organizing Tailwind Classes

For better readability, you can organize your classes by functionality:

```tsx
<button
  className={`
    /* Base styles */
    px-4 py-2 rounded font-medium
    /* Colors */
    bg-blue-500 text-white
    /* Interactive states */
    hover:bg-blue-600 focus:outline-none focus:ring-2 focus:ring-blue-400
    /* Transitions */
    transition-colors duration-200
    /* Responsive adjustments */
    w-full md:w-auto
  `}
>
  Organized Button
</button>
```

## Extracting Components for Reusability

To avoid repeating the same utility combinations, you can:

### 1. Extract React Components

```tsx
// Button.tsx
interface ButtonProps {
  children: React.ReactNode;
  primary?: boolean;
  onClick?: () => void;
}

export function Button({ children, primary = true, onClick }: ButtonProps) {
  return (
    <button
      onClick={onClick}
      className={`
        px-4 py-2 rounded font-medium
        ${
          primary
            ? "bg-blue-500 hover:bg-blue-600 text-white"
            : "bg-gray-200 hover:bg-gray-300 text-gray-800"
        }
        transition-colors
      `}
    >
      {children}
    </button>
  );
}
```

### 2. Using @apply for Component Classes

In your CSS file, you can create reusable components with Tailwind's `@apply` directive:

```css
@tailwind base;
@tailwind components;
@tailwind utilities;

@layer components {
  .btn {
    @apply px-4 py-2 rounded font-medium transition-colors;
  }

  .btn-primary {
    @apply bg-blue-500 hover:bg-blue-600 text-white;
  }

  .btn-secondary {
    @apply bg-gray-200 hover:bg-gray-300 text-gray-800;
  }

  .card {
    @apply bg-white rounded-lg shadow-md overflow-hidden;
  }

  .form-input {
    @apply w-full border border-gray-300 rounded px-4 py-2 focus:border-blue-500 focus:ring focus:ring-blue-200;
  }
}
```

Then use these classes in your components:

```tsx
<button className="btn btn-primary">Primary Button</button>
<button className="btn btn-secondary">Secondary Button</button>
<div className="card">
  <div className="p-4">Card Content</div>
</div>
```

## Customizing Tailwind

You can customize Tailwind in the `tailwind.config.js` file:

```js
/** @type {import('tailwindcss').Config} */
module.exports = {
  content: ["./index.html", "./src/**/*.{js,ts,jsx,tsx}"],
  theme: {
    extend: {
      colors: {
        // Add your brand colors
        brand: {
          light: "#7dd3fc", // light blue
          DEFAULT: "#0ea5e9", // sky blue
          dark: "#0369a1", // dark blue
        },
      },
      borderRadius: {
        lg: "1rem", // Bigger border radius
      },
      fontFamily: {
        sans: ["Inter", "sans-serif"], // Custom font
      },
      spacing: {
        18: "4.5rem", // Custom spacing
      },
    },
  },
  plugins: [],
};
```

## Dark Mode

Tailwind supports dark mode with the `dark:` prefix:

```tsx
<div className="bg-white dark:bg-gray-800 text-gray-800 dark:text-gray-200">
  This changes in dark mode
</div>
```

Enable dark mode in your `tailwind.config.js`:

```js
/** @type {import('tailwindcss').Config} */
module.exports = {
  darkMode: "class", // or 'media' for system preferences
  // ...rest of config
};
```

To toggle dark mode with JavaScript:

```tsx
function toggleDarkMode() {
  if (document.documentElement.classList.contains("dark")) {
    document.documentElement.classList.remove("dark");
    localStorage.theme = "light";
  } else {
    document.documentElement.classList.add("dark");
    localStorage.theme = "dark";
  }
}

// Check user preference on page load
if (
  localStorage.theme === "dark" ||
  (!("theme" in localStorage) &&
    window.matchMedia("(prefers-color-scheme: dark)").matches)
) {
  document.documentElement.classList.add("dark");
} else {
  document.documentElement.classList.remove("dark");
}
```

## Best Practices

1. **Use a consistent spacing system**: Stick to Tailwind's spacing scale.

2. **Mobile-first approach**: Design for mobile first, then add responsive variants.

3. **Extract components**: Don't repeat the same utility combinations.

4. **Keep accessibility in mind**: Use appropriate text contrast and semantic HTML.

5. **Use arbitrary values sparingly**: Tailwind lets you use custom values like `w-[327px]`, but try to stick to the design system when possible.

6. **Group related classes**: Organize your classes for better readability.

7. **Consider customizing Tailwind**: Create a consistent design system based on your brand.

Tailwind offers a different way of styling your applications that eliminates the need for writing custom CSS in most cases, allowing you to build UIs quickly and consistently.
