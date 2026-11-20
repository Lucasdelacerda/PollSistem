export function AuthForm({ title, subtitle, children, onSubmit }) {
  return (
    <main className="grid min-h-screen place-items-center bg-[radial-gradient(circle_at_top,_rgba(139,92,246,0.14),_transparent_38%)] px-4 pt-16">
      <div className="w-full max-w-md rounded-3xl border border-violet-100 bg-white p-7 shadow-[0_24px_70px_-30px_rgba(124,58,237,0.35)] sm:p-9">
        <div className="mb-8">
          <p className="mb-2 text-sm font-semibold text-violet-600">Enquete</p>
          <h1 className="text-3xl font-bold tracking-tight text-zinc-950">{title}</h1>
          <p className="mt-2 text-sm text-zinc-500">{subtitle}</p>
        </div>
        <form className="space-y-5" onSubmit={onSubmit}>{children}</form>
      </div>
    </main>
  )
}
