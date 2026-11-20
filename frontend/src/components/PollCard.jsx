import { Link } from 'react-router-dom'
import { ArrowRight, CalendarDays, CircleDot } from 'lucide-react'

export function PollCard({ poll }) {
  const finishDate = new Date(poll.finishDate).toLocaleDateString('pt-BR', {
    day: '2-digit',
    month: 'short',
  })

  return (
    <article className="group flex min-h-52 flex-col rounded-2xl border border-violet-100 bg-white p-5 shadow-[0_10px_30px_-12px_rgba(124,58,237,0.22)] transition duration-300 hover:-translate-y-1 hover:border-violet-300 hover:shadow-[0_18px_40px_-16px_rgba(124,58,237,0.38)]">
      <div className="mb-5 flex items-center justify-between">
        <span className={`inline-flex items-center gap-1.5 rounded-full px-2.5 py-1 text-xs font-semibold ${poll.status === 'OPEN' ? 'bg-violet-50 text-violet-700' : 'bg-zinc-100 text-zinc-600'}`}>
          <CircleDot size={12} />
          {poll.status === 'OPEN' ? 'Aberta' : poll.status}
        </span>
        <span className="flex items-center gap-1.5 text-xs text-zinc-500">
          <CalendarDays size={14} /> {finishDate}
        </span>
      </div>

      <h2 className="text-lg font-bold leading-snug text-zinc-900">{poll.title}</h2>
      <p className="mt-2 line-clamp-2 text-sm leading-6 text-zinc-500">{poll.question}</p>

      <Link to={`/polls/${poll.id}`} className="mt-auto flex items-center gap-2 pt-5 text-sm font-semibold text-violet-600">
        Ver enquete
        <ArrowRight size={16} className="transition group-hover:translate-x-1" />
      </Link>
    </article>
  )
}
