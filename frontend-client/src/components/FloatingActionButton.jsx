import React from 'react';
import { Bot } from 'lucide-react';

export default function FloatingActionButton({ onClick }) {
  return (
    <button
      onClick={onClick}
      title="Ouvrir l'assistant IA"
      className="fixed bottom-6 right-6 z-40 flex items-center gap-2 px-4 py-3 bg-teal-600 hover:bg-teal-700 text-white rounded-full shadow-lg hover:shadow-teal-600/30 active:scale-95 transition-all duration-300 group border border-teal-500/30"
    >
      <Bot className="w-5 h-5 flex-shrink-0" />
      <span className="max-w-0 opacity-0 overflow-hidden whitespace-nowrap group-hover:max-w-xs group-hover:opacity-100 transition-all duration-300 text-xs font-medium">
        Assistant IA & Outils
      </span>
    </button>
  );
}

