package org.example;

import java.util.Spliterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class StreamZipper {
    public static <T> Stream<T> zip(Stream<T> first, Stream<T> second) {

        Spliterator<T> zipSpliterator = new ZipSpliterator<>(first.spliterator(), second.spliterator());


        return StreamSupport.stream(zipSpliterator, false)
                .onClose(() -> {
                    first.close();
                    second.close();
                });
    }


        private record ZipSpliterator<T>(Spliterator<T> firstSpliterator,
                                         Spliterator<T> secondSpliterator) implements Spliterator<T> {

        @Override
            public boolean tryAdvance(java.util.function.Consumer<? super T> action) {

                boolean hadNextFirst = firstSpliterator.tryAdvance(action);
                boolean hadNextSecond = secondSpliterator.tryAdvance(action);
                return hadNextFirst && hadNextSecond;
            }

            @Override
            public Spliterator<T> trySplit() {
                return null;
            }

            @Override
            public long estimateSize() {
                return Math.min(firstSpliterator.estimateSize(), secondSpliterator.estimateSize());
            }

            @Override
            public int characteristics() {
                return firstSpliterator.characteristics() & secondSpliterator.characteristics()
                        & ~(Spliterator.SORTED | Spliterator.DISTINCT); 
            }
        }

    public static void main(String[] args) {
        Stream<Integer> stream1 = Stream.of(1, 2, 3, 4, 5);
        Stream<Integer> stream2 = Stream.of(6, 7, 8);

        Stream<Integer> zippedStream = zip(stream1, stream2);
        zippedStream.forEach(System.out::println);
    }
}
