import { UserDropdown } from "@/features/auth/components/user-dropdown";

export const Navbar = () => {
  return (
    <nav className="py-4 bg-slate-300 border-b">
      <div className="flex items-center justify-between max-w-screen mx-auto w-11/12">
        <ul>
          <h1 className="font-bold text-2xl">YOLP</h1>
        </ul>
        <ul>
          <UserDropdown />
        </ul>
      </div>
    </nav>
  );
};
