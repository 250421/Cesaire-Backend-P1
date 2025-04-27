import { z } from "zod";

export const signupSchema = z.object({
  username: z.string().min(1, {
    message: "Username is required",
  }),
  password: z.string().min(1, {
    message: "Password is required",
  }),
});

export type SignupSchemaType = z.infer<typeof signupSchema>;
