import { useSidebar } from "@/hooks/use-sidebar";
import React from "react";

interface SidebarContainerProps {
  children: React.ReactNode;
}

export const SidebarContainer = ({ children }: SidebarContainerProps) => {
  const { mounted } = useSidebar();

  if (!mounted) return null;

  return <div className="h-[200vh]">{children}</div>;
};
