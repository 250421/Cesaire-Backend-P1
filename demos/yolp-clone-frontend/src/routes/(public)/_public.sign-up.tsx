import { createFileRoute, Link } from "@tanstack/react-router";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import {
  signupSchema,
  SignupSchemaType,
} from "@/features/auth/schemas/sign-up-schema";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { useSignUp } from "@/features/auth/hooks/use-sign-up";
import { Loader2 } from "lucide-react";

export const Route = createFileRoute("/(public)/_public/sign-up")({
  component: SignUpPage,
});

function SignUpPage() {
  const { mutate: createUser, isPending } = useSignUp();

  const form = useForm<SignupSchemaType>({
    resolver: zodResolver(signupSchema),
    defaultValues: {
      username: "",
      password: "",
    },
  });

  function onSubmit(values: SignupSchemaType) {
    console.log(values);
    createUser(values);
  }

  return (
    <Card className="w-[400px]">
      <CardHeader>
        <CardTitle className="font-bold text-2xl">Register</CardTitle>
        <CardDescription>Please fill in the details below:</CardDescription>
      </CardHeader>
      <CardContent>
        <Form {...form}>
          <form
            onSubmit={form.handleSubmit(onSubmit)}
            className="flex flex-col gap-y-6"
          >
            <FormField
              control={form.control}
              name="username"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Username</FormLabel>
                  <FormControl>
                    <Input placeholder="Username" {...field} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            <FormField
              control={form.control}
              name="password"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Password</FormLabel>
                  <FormControl>
                    <Input type="password" placeholder="Password" {...field} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
            <Button type="submit" disabled={isPending}>
              {isPending ? (
                <Loader2 className="size-4 animate-spin" />
              ) : (
                "Submit"
              )}
            </Button>
          </form>
        </Form>

        <div className="flex items-center gap-x-2 pt-4">
          <p>Already have an account?</p>
          <Link to={"/sign-in"} className="text-blue-500 underline">
            Sign In
          </Link>
        </div>
      </CardContent>
    </Card>
  );
}
