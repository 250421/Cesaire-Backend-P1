import { useMutation } from "@tanstack/react-query";
import { SignupSchemaType } from "../schemas/sign-up-schema";
import { axiosInstance } from "@/lib/axios-config";
import { AxiosError } from "axios";
import { toast } from "sonner";
import { useNavigate } from "@tanstack/react-router";

export const useSignUp = () => {
  const navigate = useNavigate();

  return useMutation({
    mutationFn: async (values: SignupSchemaType) => {
      const resp = await axiosInstance.post("/auth/sign-up", values);
      return resp.data;
    },
    onSuccess: () => {
      toast.success("User created");
      navigate({ to: "/sign-in" });
    },
    onError: (error) => {
      console.error(error);
      if (error instanceof AxiosError) {
        toast.error(error.response?.data.message);
      }
    },
  });
};
