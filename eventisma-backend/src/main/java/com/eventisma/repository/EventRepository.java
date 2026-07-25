package com.eventisma.repository;

import com.eventisma.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, String> {

    List<Event> findByIsWorkshopTrue();

    List<Event> findByCategoryIgnoreCase(String category);
}
