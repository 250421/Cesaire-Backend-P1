import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { UserProfile } from "./user-profile";
import { useSignOut } from "../hooks/use-sign-out";
import { useConfirm } from "@/hooks/use-confirm";

export const UserDropdown = () => {
  const [logOutConfirm, LogOutDialog] = useConfirm();

  const { mutate: signOut } = useSignOut();

  const handleLogOut = async () => {
    const ok = await logOutConfirm();
    if (!ok) return;

    signOut();
  };

  return (
    <>
      <DropdownMenu>
        <DropdownMenuTrigger>
          <UserProfile />
        </DropdownMenuTrigger>
        <DropdownMenuContent align="end">
          <DropdownMenuItem onClick={handleLogOut}>Log out</DropdownMenuItem>
        </DropdownMenuContent>
      </DropdownMenu>

      <LogOutDialog
        title={"Log Out"}
        description={"Are you sure you want to log out?"}
        destructive
      />
    </>
  );
};
