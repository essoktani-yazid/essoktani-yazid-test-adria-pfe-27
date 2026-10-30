import React from 'react';
import { CreditCard, ShoppingBag, Send } from 'lucide-react';

export default function TransactionsSection({ transactions, onOpenNewTransaction }) {
  return (
    <div className="bg-white rounded-xl border border-slate-200/90 shadow-sm overflow-hidden mb-8">
      {/* Contextual Toolbar */}
      <div className="p-5 border-b border-slate-200 flex flex-col md:flex-row items-start md:items-center justify-between gap-4 bg-white">
        <div>
          <h3 className="font-semibold text-slate-900 text-sm">Registre des Transactions & Kafka Streams</h3>
          <p className="text-xs text-slate-500 mt-0.5">Événements publiés vers topic `bank.transactions.created`</p>
        </div>

        <button
          onClick={onOpenNewTransaction}
          className="flex items-center gap-1.5 px-3 py-2 rounded-lg bg-teal-600 hover:bg-teal-700 active:scale-95 text-white text-xs font-medium transition shadow-sm"
        >
          <CreditCard className="w-3.5 h-3.5" />
          <span>Nouvelle Transaction</span>
        </button>
      </div>

      {/* Table */}
      <div className="overflow-x-auto">
        <table className="w-full text-left text-xs">
          <thead>
            <tr className="bg-slate-50/80 border-b border-slate-200 text-slate-500 font-medium uppercase tracking-wider">
              <th className="px-5 py-3.5">Transaction ID</th>
              <th className="px-5 py-3.5">Compte Associé</th>
              <th className="px-5 py-3.5">Montant</th>
              <th className="px-5 py-3.5">Catégorie Marchand</th>
              <th className="px-5 py-3.5">Pays</th>
              <th className="px-5 py-3.5">Date & Heure</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-100">
            {transactions.map((tx) => (
              <tr key={tx.id} className="hover:bg-slate-50/60 transition-colors">
                <td className="px-5 py-4 font-mono text-[11px] text-slate-400 select-all">
                  {tx.id ? tx.id.substring(0, 8) + '...' : 'N/A'}
                </td>
                <td className="px-5 py-4 font-mono text-[11px] text-slate-600 select-all">
                  {tx.accountId ? tx.accountId.substring(0, 8) + '...' : 'N/A'}
                </td>
                <td className="px-5 py-4 font-mono font-bold text-slate-900 text-sm">
                  {parseFloat(tx.amount).toLocaleString('fr-FR', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}
                  <span className="text-xs font-normal text-slate-500 ml-1">{tx.currency}</span>
                </td>
                <td className="px-5 py-4">
                  <span className="inline-flex items-center gap-1 px-2 py-0.5 rounded text-[11px] font-medium bg-slate-100 text-slate-700 border border-slate-200">
                    <ShoppingBag className="w-3 h-3 text-slate-500" />
                    {tx.merchantCategory}
                  </span>
                </td>
                <td className="px-5 py-4">
                  <span className="inline-flex items-center px-2 py-0.5 rounded text-[11px] font-mono font-bold bg-teal-50 text-teal-700 border border-teal-200">
                    {tx.country}
                  </span>
                </td>
                <td className="px-5 py-4 font-mono text-[11px] text-slate-500">
                  {new Date(tx.createdAt).toLocaleString('fr-FR')}
                </td>
              </tr>
            ))}
            {transactions.length === 0 && (
              <tr>
                <td colSpan="6" className="px-5 py-8 text-center text-slate-400">
                  Aucune transaction enregistrée.
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}

