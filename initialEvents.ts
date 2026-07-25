import React, { useState } from 'react';
import { Navbar } from './components/Navbar';
import { HeroBanner } from './components/HeroBanner';
import { EventCard } from './components/EventCard';
import { EventDetailModal } from './components/EventDetailModal';
import { TicketCheckoutModal } from './components/TicketCheckoutModal';
import { ScheduleTracker } from './components/ScheduleTracker';
import { MyTicketsVault } from './components/MyTicketsVault';
import { AddEventModal } from './components/AddEventModal';
import { initialEvents } from './data/initialEvents';
import { EventItem, Ticket } from './types';
import { Filter, Sparkles, CheckCircle2, AlertCircle, Zap } from 'lucide-react';

export function App() {
  const [activeTab, setActiveTab] = useState<'explore' | 'workshops' | 'schedule' | 'tickets'>('explore');
  const [events, setEvents] = useState<EventItem[]>(initialEvents);
  const [userTickets, setUserTickets] = useState<Ticket[]>([]);
  const [searchQuery, setSearchQuery] = useState<string>('');
  const [selectedCategory, setSelectedCategory] = useState<string>('all');
  const [selectedDay, setSelectedDay] = useState<number | 'all'>('all');
  const [priceType, setPriceType] = useState<'all' | 'free' | 'paid'>('all');
  const [workshopsOnly, setWorkshopsOnly] = useState<boolean>(false);
  const [selectedEventModal, setSelectedEventModal] = useState<EventItem | null>(null);
  const [checkoutEventModal, setCheckoutEventModal] = useState<EventItem | null>(null);
  const [isAddEventOpen, setIsAddEventOpen] = useState<boolean>(false);
  const [toastMessage, setToastMessage] = useState<string | null>(null);

  const showToast = (msg: string) => {
    setToastMessage(msg);
    setTimeout(() => setToastMessage(null), 3000);
  };

  const handleTicketSuccess = (ticket: Ticket) => {
    setUserTickets((prev) => [ticket, ...prev]);
    setEvents((prev) =>
      prev.map((ev) =>
        ev.id === ticket.eventId
          ? { ...ev, registeredSlots: ev.registeredSlots + 1 }
          : ev
      )
    );
    showToast(`Pass confirmed for ${ticket.eventTitle}!`);
  };

  const handleCancelTicket = (ticketId: string) => {
    const tkt = userTickets.find((t) => t.id === ticketId);
    if (tkt) {
      setUserTickets((prev) => prev.filter((t) => t.id !== ticketId));
      setEvents((prev) =>
        prev.map((ev) =>
          ev.id === tkt.eventId
            ? { ...ev, registeredSlots: Math.max(0, ev.registeredSlots - 1) }
            : ev
        )
      );
      showToast('Ticket pass cancelled.');
    }
  };

  const filteredEvents = events.filter((e) => {
    if (activeTab === 'workshops' && !e.isWorkshop) return false;
    if (workshopsOnly && !e.isWorkshop) return false;
    if (selectedCategory !== 'all' && e.category !== selectedCategory) return false;
    if (selectedDay !== 'all' && e.dayNumber !== selectedDay) return false;
    if (priceType === 'free' && e.price > 0) return false;
    if (priceType === 'paid' && e.price === 0) return false;
    if (searchQuery.trim()) {
      const q = searchQuery.toLowerCase();
      const matchTitle = e.title.toLowerCase().includes(q);
      const matchVenue = e.venue.toLowerCase().includes(q);
      const matchSpeaker = e.speaker.name.toLowerCase().includes(q);
      const matchCat = e.category.toLowerCase().includes(q);
      return matchTitle || matchVenue || matchSpeaker || matchCat;
    }
    return true;
  });

  return (
    <div className="min-h-screen bg-[#050505] text-[#e0e0e0] font-sans">
      <Navbar
        activeTab={activeTab}
        setActiveTab={setActiveTab}
        ticketCount={userTickets.length}
        searchQuery={searchQuery}
        setSearchQuery={setSearchQuery}
        onOpenAddEvent={() => setIsAddEventOpen(true)}
      />
      {activeTab === 'explore' && (
        <HeroBanner
          onExplore={() => setActiveTab('workshops')}
          onOpenAddEvent={() => setIsAddEventOpen(true)}
        />
      )}
      {/* Main Event Catalog & Views */}
      <main className="max-w-7xl mx-auto px-4 py-8">
        {activeTab === 'schedule' ? (
          <ScheduleTracker
            events={events}
            registeredEventIds={userTickets.map((t) => t.eventId)}
            onSelectEvent={(ev) => setSelectedEventModal(ev)}
            onRegisterEvent={(ev) => setCheckoutEventModal(ev)}
          />
        ) : activeTab === 'tickets' ? (
          <MyTicketsVault
            tickets={userTickets}
            onCancelTicket={handleCancelTicket}
            onExploreEvents={() => setActiveTab('explore')}
          />
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {filteredEvents.map((ev) => (
              <EventCard
                key={ev.id}
                event={ev}
                onSelect={(e) => setSelectedEventModal(e)}
                onRegister={(e) => setCheckoutEventModal(e)}
                isUserRegistered={userTickets.some((t) => t.eventId === ev.id)}
              />
            ))}
          </div>
        )}
      </main>
      {/* Modals */}
      {selectedEventModal && (
        <EventDetailModal
          event={selectedEventModal}
          onClose={() => setSelectedEventModal(null)}
          onBookPass={(ev) => {
            setSelectedEventModal(null);
            setCheckoutEventModal(ev);
          }}
          isRegistered={userTickets.some((t) => t.eventId === selectedEventModal.id)}
        />
      )}
      {checkoutEventModal && (
        <TicketCheckoutModal
          event={checkoutEventModal}
          onClose={() => setCheckoutEventModal(null)}
          onSuccess={handleTicketSuccess}
        />
      )}
      {isAddEventOpen && (
        <AddEventModal
          onClose={() => setIsAddEventOpen(false)}
          onAddEvent={(newEvent) => {
            setEvents((prev) => [newEvent, ...prev]);
            showToast('New event created successfully!');
          }}
        />
      )}
    </div>
  );
}
