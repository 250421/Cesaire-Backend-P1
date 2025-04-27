import { useMutation } from "@tanstack/react-query";
import { SignInSchemaType } from "../schemas/sign-in-schema";
import { axiosInstance } from "@/lib/axios-config";
import { toast } from "sonner";
import { useNavigate } from "@tanstack/react-router";
import { AxiosError } from "axios";

export const useSignIn = () => {
  const navigate = useNavigate();

  return useMutation({
    mutationFn: async (values: SignInSchemaType) => {
      const resp = await axiosInstance.post("/auth/sign-in", values);
      return resp.data;
    },
    onSuccess: () => {
      toast.success("User logged in");
      navigate({ to: "/" });
    },
    onError: (error) => {
      console.error(error);
      if (error instanceof AxiosError) {
        toast.error(error.response?.data.message);
      }
    },
  });
};
