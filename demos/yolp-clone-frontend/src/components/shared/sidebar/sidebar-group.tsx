import React from "react";

import { useSidebar } from "@/hooks/use-sidebar";
import { cn } from "@/lib/utils";

interface SidebarGroupProps {
  children: React.ReactNode;
}

export const SidebarGroup = ({ children }: SidebarGroupProps) => {
  const { isOpen } = useSidebar();

  return (
    <div
      className={cn(
        "transition-all duration-200 ease-in-out px-3 py-2 flex flex-col gap-y-2",
        isOpen ? "opacity-100" : "opacity-0 pointer-events-none"
      )}
    >
      {children}
    </div>
  );
};
