import React from "react";

interface SidebarContainerProps {
  children: React.ReactNode;
}

export const SidebarContent = ({ children }: SidebarContainerProps) => {
  return <div className="flex flex-col gap-y-10">{children}</div>;
};
