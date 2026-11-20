import { PollCard } from './PollCard'

export function PollGrid({ polls }) {
  if (!polls.length) {
    return (
      <div className="rounded-2xl border border-dashed border-violet-200 bg-violet-50/40 px-6 py-16 text-center">
        <p className="font-semibold text-zinc-800">Nenhuma enquete encontrada</p>
        <p className="mt-1 text-sm text-zinc-500">Seja o primeiro a criar uma votação.</p>
      </div>
    )
  }

  return (
    <div className="grid gap-5 sm:grid-cols-2 lg:grid-cols-3">
      {polls.map((poll) => <PollCard key={poll.id} poll={poll} />)}
    </div>
  )
}
