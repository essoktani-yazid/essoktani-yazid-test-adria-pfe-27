import React from 'react';
import { Wallet, ArrowLeftRight, ShieldAlert, Cpu } from 'lucide-react';

export default function BentoMetrics({ accounts, transactions }) {
  const totalBalanceMAD = accounts
    .filter(a => a.currency === 'MAD')
    .reduce((acc, curr) => acc + (parseFloat(curr.balance) || 0), 0);

  const totalBalanceEUR = accounts
    .filter(a => a.currency === 'EUR')
    .reduce((acc, curr) => acc + (parseFloat(curr.balance) || 0), 0);

  const activeAccountsCount = accounts.filter(a => a.status === 'ACTIVE').length;

  return (
    <div className="grid grid-cols-2 lg:grid-cols-4 gap-3.5 mb-6">
      {/* Metric 1 */}
      <div className="bg-white p-4 sm:p-5 rounded-xl border border-slate-200/90 shadow-sm flex flex-col justify-between hover:border-slate-300 hover:shadow hover:-translate-y-0.5 transition-all duration-200">
        <div className="flex items-center justify-between">
          <span className="font-mono text-xs font-bold text-slate-700 bg-slate-100 px-2 py-0.5 rounded border border-slate-200">
            SOLDE MAD
          </span>
          <div className="w-7 h-7 rounded-lg bg-teal-50 border border-teal-200 flex items-center justify-center text-teal-600">
            <Wallet className="w-3.5 h-3.5" />
          </div>
        </div>
        <div className="mt-3">
          <p className="text-xl sm:text-2xl font-mono font-bold text-slate-900 tracking-tight">
            {totalBalanceMAD.toLocaleString('fr-FR', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}
            <span className="text-xs font-normal text-slate-500 ml-1">MAD</span>
          </p>
          <p className="text-xs text-slate-500 truncate mt-0.5">Encours global en Dirhams</p>
        </div>
      </div>

      {/* Metric 2 */}
      <div className="bg-white p-4 sm:p-5 rounded-xl border border-slate-200/90 shadow-sm flex flex-col justify-between hover:border-slate-300 hover:shadow hover:-translate-y-0.5 transition-all duration-200">
        <div className="flex items-center justify-between">
          <span className="font-mono text-xs font-bold text-slate-700 bg-slate-100 px-2 py-0.5 rounded border border-slate-200">
            SOLDE EUR
          </span>
          <div className="w-7 h-7 rounded-lg bg-teal-50 border border-teal-200 flex items-center justify-center text-teal-600">
            <Wallet className="w-3.5 h-3.5" />
          </div>
        </div>
        <div className="mt-3">
          <p className="text-xl sm:text-2xl font-mono font-bold text-slate-900 tracking-tight">
            {totalBalanceEUR.toLocaleString('fr-FR', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}
            <span className="text-xs font-normal text-slate-500 ml-1">EUR</span>
          </p>
          <p className="text-xs text-slate-500 truncate mt-0.5">Comptes devises étrangères</p>
        </div>
      </div>

      {/* Metric 3 */}
      <div className="bg-white p-4 sm:p-5 rounded-xl border border-slate-200/90 shadow-sm flex flex-col justify-between hover:border-slate-300 hover:shadow hover:-translate-y-0.5 transition-all duration-200">
        <div className="flex items-center justify-between">
          <span className="font-mono text-xs font-bold text-emerald-700 bg-emerald-50 px-2 py-0.5 rounded border border-emerald-200">
            COMPTES
          </span>
          <div className="w-7 h-7 rounded-lg bg-emerald-50 border border-emerald-200 flex items-center justify-center text-emerald-600">
            <Cpu className="w-3.5 h-3.5" />
          </div>
        </div>
        <div className="mt-3">
          <p className="text-xl sm:text-2xl font-mono font-bold text-slate-900 tracking-tight">
            {activeAccountsCount} <span className="text-xs font-normal text-slate-500">/ {accounts.length}</span>
          </p>
          <p className="text-xs text-slate-500 truncate mt-0.5">Comptes opérationnels actifs</p>
        </div>
      </div>

      {/* Metric 4 */}
      <div className="bg-white p-4 sm:p-5 rounded-xl border border-slate-200/90 shadow-sm flex flex-col justify-between hover:border-slate-300 hover:shadow hover:-translate-y-0.5 transition-all duration-200">
        <div className="flex items-center justify-between">
          <span className="font-mono text-xs font-bold text-purple-700 bg-purple-50 px-2 py-0.5 rounded border border-purple-200">
            CONFIG
          </span>
          <div className="w-7 h-7 rounded-lg bg-purple-50 border border-purple-200 flex items-center justify-center text-purple-600">
            <ShieldAlert className="w-3.5 h-3.5" />
          </div>
        </div>
        <div className="mt-3">
          <p className="text-xl sm:text-2xl font-mono font-bold text-slate-900 tracking-tight">
            1 000.00 <span className="text-xs font-normal text-slate-500">MAD</span>
          </p>
          <p className="text-xs text-slate-500 truncate mt-0.5">Plafond Spring Cloud Config</p>
        </div>
      </div>
    </div>
  );
}

