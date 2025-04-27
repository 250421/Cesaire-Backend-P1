import { Sidebar } from "lucide-react";
import React from "react";

import { useSidebar } from "@/hooks/use-sidebar";
import { cn } from "@/lib/utils";
import { Button } from "@/components/ui/button";

interface SidebarMainWrapperProps {
  children: React.ReactNode;
}

export const SidebarMainWrapper = ({ children }: SidebarMainWrapperProps) => {
  const { isOpen, toggle } = useSidebar();
  return (
    <div
      className={cn(
        "transition-all duration-300 ease-in-out",
        isOpen ? "ml-64" : "ml-0"
      )}
    >
      <Button
        variant={"ghost"}
        size={"icon"}
        onClick={toggle}
        className="fixed mx-3 top-20 z-50 cursor-pointer"
      >
        <Sidebar />
      </Button>
      {children}
    </div>
  );
};
