import React from 'react';
import { Landmark, ShieldCheck } from 'lucide-react';

export default function Header() {
  return (
    <header className="w-full bg-white border-b border-slate-200/80 sticky top-0 z-40 shadow-xs">
      <div className="w-full px-6 h-16 flex items-center justify-between">
        {/* Logo & Application Title */}
        <div className="flex items-center gap-3">
          <div className="w-9 h-9 rounded-lg bg-teal-50 border border-teal-200 flex items-center justify-center text-teal-700 shadow-xs">
            <Landmark className="w-5 h-5" />
          </div>
          <div className="flex items-center gap-2">
            <span className="font-bold text-sm tracking-tight text-slate-900">ADRIA BANK</span>
            <span className="text-slate-300">/</span>
            <span className="text-xs text-slate-600 font-medium hidden sm:inline">Portail Microservices & IA</span>
          </div>
        </div>

        {/* User Profile Component */}
        <div className="flex items-center gap-2.5 px-3 py-1.5 rounded-xl bg-slate-50 border border-slate-200/90 shadow-sm">
          <div className="relative">
            <div className="w-8 h-8 rounded-lg bg-teal-600 text-white flex items-center justify-center font-bold text-xs shadow-sm">
              AD
            </div>
            <span className="absolute -bottom-0.5 -right-0.5 w-2.5 h-2.5 rounded-full bg-emerald-500 border-2 border-white ring-1 ring-emerald-400/30" />
          </div>
          <div className="text-left hidden sm:block">
            <div className="flex items-center gap-1.5">
              <span className="text-xs font-semibold text-slate-900 leading-tight">Administrateur</span>
              <span className="text-[10px] px-1.5 py-0.5 rounded bg-emerald-50 text-emerald-700 border border-emerald-200 font-medium leading-none">Actif</span>
            </div>
            <span className="text-[11px] text-slate-500 font-mono leading-tight block mt-0.5">admin@enterprise.com</span>
          </div>
        </div>
      </div>
    </header>
  );
}

