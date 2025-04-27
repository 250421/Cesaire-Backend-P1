import { Home, List, Settings } from "lucide-react";
import { Sidebar } from "./sidebar";
import { SidebarContent } from "./sidebar-content";
import { SidebarGroup } from "./sidebar-group";
import { SidebarItem } from "./sidebar-item";

export const AppSidebar = () => {
  return (
    <Sidebar>
      <SidebarContent>
        <SidebarGroup>
          <SidebarItem label={"Dashboard"} icon={Home} href={"/dashboard"} />
          <SidebarItem label={"Products"} icon={List} href={"/products"} />
          <SidebarItem label={"Settings"} icon={Settings} href={"/settings"} />
        </SidebarGroup>
      </SidebarContent>
    </Sidebar>
  );
};
