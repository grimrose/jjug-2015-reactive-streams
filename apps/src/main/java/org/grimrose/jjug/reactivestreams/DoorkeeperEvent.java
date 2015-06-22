package org.grimrose.jjug.reactivestreams;

public class DoorkeeperEvent {

    public Event event;

    public static class Event {
        public Long id;
        public String title;
        public String starts_at;
        public String ends_at;
        public Group group;

        @Override
        public String toString() {
            return "Event{" +
                    "id=" + id +
                    ", title='" + title + '\'' +
                    ", starts_at='" + starts_at + '\'' +
                    ", ends_at='" + ends_at + '\'' +
                    ", group=" + group +
                    '}';
        }
    }


    public static class Group {
        public Long id;
        public String name;

        @Override
        public String toString() {
            return "Group{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "DoorkeeperEvent{" +
                "event=" + event +
                '}';
    }
}
