@import "tailwindcss";

@layer base {
  body {
    background-color: #050505;
    color: #e0e0e0;
    font-family: system-ui, -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
    overflow-x: hidden;
  }
}

.cyber-grid-bg {
  background-image:
    linear-gradient(to right, rgba(0, 243, 255, 0.04) 1px, transparent 1px),
    linear-gradient(to bottom, rgba(0, 243, 255, 0.04) 1px, transparent 1px);
  background-size: 32px 32px;
}

.neon-glow-cyan {
  box-shadow: 0 0 15px rgba(0, 243, 255, 0.35), inset 0 0 10px rgba(0, 243, 255, 0.1);
}

.clip-cyber-corner {
  clip-path: polygon(
    0 0,
    calc(100% - 12px) 0,
    100% 12px,
    100% 100%,
    12px 100%,
    0 calc(100% - 12px)
  );
}

@media print {
  body {
    background: white !important;
    color: black !important;
  }
  .no-print {
    display: none !important;
  }
  .print-only {
    display: block !important;
  }
}
