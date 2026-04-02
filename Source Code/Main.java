import java.util.*;
import java.io.*;

class Event {
    protected String eventName;
    protected String date;
    protected List<String> participants;

    public Event(String eventName, String date) {
        this.eventName = eventName;
        this.date = date;
        participants = new ArrayList<>();
    }

    public synchronized void registerParticipant(String name) {
        participants.add(name);
        System.out.println(name + " registered for " + eventName);
    }

    public String getEventName() {
        return eventName;
    }

    public String getDate() {
        return date;
    }

    public List<String> getParticipants() {
        return participants;
    }
}

class Conference extends Event {
    public Conference(String eventName, String date) {
        super(eventName, date);
    }
}

class Workshop extends Event {
    public Workshop(String eventName, String date) {
        super(eventName, date);
    }
}

class EventManager {

    private Map<String, Event> events = new HashMap<>();

 
    public void addEvent(Event e) throws Exception {
        if (events.containsKey(e.getDate())) {
            throw new Exception("Schedule conflict on " + e.getDate());
        }
        events.put(e.getDate(), e);
    }

    class Scheduler {
        public void displaySchedule() {
            System.out.println("\n--- Event Schedule ---");
            for (String date : events.keySet()) {
                System.out.println(date + " -> " + events.get(date).getEventName());
            }
        }
    }

    class RegistrationThread extends Thread {
        Event event;
        String name;

        public RegistrationThread(Event event, String name) {
            this.event = event;
            this.name = name;
        }

        public void run() {
            event.registerParticipant(name);
        }
    }

    public void saveToFile() {
        try {
            FileWriter fw = new FileWriter("events.txt");

            for (Event e : events.values()) {
                fw.write("Event: " + e.getEventName() + "\n");
                fw.write("Date: " + e.getDate() + "\n");
                fw.write("Participants: " + e.getParticipants() + "\n\n");
            }

            fw.close();
            System.out.println("\nData saved to events.txt");

        } catch (IOException e) {
            System.out.println("File Error: " + e.getMessage());
        }
    }

    public Map<String, Event> getEvents() {
        return events;
    }
}

public class Main {
    public static void main(String[] args) {

        EventManager manager = new EventManager();

        try {
          
            Event e1 = new Conference("Tech Conference", "10-05-2026");
            Event e2 = new Workshop("AI Workshop", "18-05-2026");

            manager.addEvent(e1);
            manager.addEvent(e2);

          
            EventManager.RegistrationThread t1 = manager.new RegistrationThread(e1, "Alice");
            EventManager.RegistrationThread t2 = manager.new RegistrationThread(e1, "Bob");

            t1.start();
            t2.start();

     
            t1.join();
            t2.join();

            EventManager.Scheduler scheduler = manager.new Scheduler();
            scheduler.displaySchedule();

        
            manager.saveToFile();

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
