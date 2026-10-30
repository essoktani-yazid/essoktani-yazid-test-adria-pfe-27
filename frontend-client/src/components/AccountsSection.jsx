import React from 'react';
import { PlusCircle, ArrowLeftRight, UserCheck, Shield } from 'lucide-react';

export default function AccountsSection({ accounts, onOpenNewAccount, onOpenTransfer }) {
  return (
    <div className="bg-white rounded-xl border border-slate-200/90 shadow-sm overflow-hidden mb-6">
      {/* Contextual Toolbar */}
      <div className="p-5 border-b border-slate-200 flex flex-col md:flex-row items-start md:items-center justify-between gap-4 bg-white">
        <div>
          <h3 className="font-semibold text-slate-900 text-sm">Gestion des Comptes Bancaires</h3>
          <p className="text-xs text-slate-500 mt-0.5">Microservice Account-Service (REST, GraphQL, MCP)</p>
        </div>

        <div className="flex flex-wrap items-center gap-2.5 w-full md:w-auto">
          <button
            onClick={onOpenTransfer}
            className="flex items-center gap-1.5 px-3 py-2 rounded-lg bg-slate-100 hover:bg-slate-200 active:scale-95 text-slate-700 text-xs font-medium transition shadow-xs"
          >
            <ArrowLeftRight className="w-3.5 h-3.5 text-teal-600" />
            <span>Effectuer un Virement</span>
          </button>

          <button
            onClick={onOpenNewAccount}
            className="flex items-center gap-1.5 px-3 py-2 rounded-lg bg-teal-600 hover:bg-teal-700 active:scale-95 text-white text-xs font-medium transition shadow-sm"
          >
            <PlusCircle className="w-3.5 h-3.5" />
            <span>Nouveau Compte</span>
          </button>
        </div>
      </div>

      {/* Table */}
      <div className="overflow-x-auto">
        <table className="w-full text-left text-xs">
          <thead>
            <tr className="bg-slate-50/80 border-b border-slate-200 text-slate-500 font-medium uppercase tracking-wider">
              <th className="px-5 py-3.5">Titulaire du Compte</th>
              <th className="px-5 py-3.5">Statut</th>
              <th className="px-5 py-3.5">Devise</th>
              <th className="px-5 py-3.5">Solde Disponible</th>
              <th className="px-5 py-3.5">Identifiant (UUID)</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-100">
            {accounts.map((acc) => (
              <tr key={acc.id} className="hover:bg-slate-50/60 transition-colors">
                <td className="px-5 py-4 font-medium text-slate-900 flex items-center gap-2">
                  <div className="w-6 h-6 rounded bg-slate-100 text-slate-600 flex items-center justify-center">
                    <UserCheck className="w-3.5 h-3.5" />
                  </div>
                  {acc.customerReference}
                </td>
                <td className="px-5 py-4">
                  {acc.status === 'ACTIVE' ? (
                    <span className="inline-flex items-center px-2 py-0.5 rounded-full text-[11px] font-medium bg-emerald-50 text-emerald-700 border border-emerald-200">
                      Actif
                    </span>
                  ) : (
                    <span className="inline-flex items-center px-2 py-0.5 rounded-full text-[11px] font-medium bg-rose-50 text-rose-700 border border-rose-200">
                      Bloqué
                    </span>
                  )}
                </td>
                <td className="px-5 py-4 font-mono font-semibold text-slate-600">
                  {acc.currency}
                </td>
                <td className="px-5 py-4 font-mono font-bold text-slate-900 text-sm">
                  {parseFloat(acc.balance).toLocaleString('fr-FR', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}
                  <span className="text-xs font-normal text-slate-500 ml-1">{acc.currency}</span>
                </td>
                <td className="px-5 py-4 font-mono text-[11px] text-slate-400 select-all">
                  {acc.id}
                </td>
              </tr>
            ))}
            {accounts.length === 0 && (
              <tr>
                <td colSpan="5" className="px-5 py-8 text-center text-slate-400">
                  Aucun compte enregistré pour le moment.
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}

