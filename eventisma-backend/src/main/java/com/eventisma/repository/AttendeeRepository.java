package com.eventisma.repository;

import com.eventisma.model.Attendee;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AttendeeRepository extends JpaRepository<Attendee, String> {

    List<Attendee> findByEventId(String eventId);

    void deleteByEventId(String eventId);
}
