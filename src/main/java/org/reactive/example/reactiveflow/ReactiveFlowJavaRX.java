package org.reactive.example.reactiveflow;

import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * reactive programing
 *
 * @author Alexander Bravo
 */
public class ReactiveFlowJavaRX
{

 private static List doSquares()
 {
     List squares = new ArrayList();
     Flowable.range(1, 64)
             //Call the method observeOn to determine which Scheduler to use. This determines on which Thread
             // or Threads the flow will run. The Scheduler returned from computation() takes advantage of all available
             // processors when possible.
             .observeOn(Schedulers.computation())
             .map(x -> x * x)
             .blockingSubscribe(squares::add);
     return squares;
 }

 private static List doParallelSquare()
 {
     List squares = new ArrayList();
     Flowable.range(1, 64)
             .flatMap(x -> Flowable.just(x)
             .subscribeOn(Schedulers.computation())
             .map(y -> y * y))
             .doOnError(ex -> ex.printStackTrace())
             .doOnComplete(() -> System.out.println(":::Complete::"))
             .blockingSubscribe(squares::add);
     return squares;
 }

    /**
     * For some heavy computations, you may want to run them in the background
     * while rendering the result in a separate thread so as not to block the UI or rendering thread.
     * For this case, you can use the subscribeOn method with one Scheduler and the observeOn method
     * with a different Scheduler.
     *
     * @throws Exception
     */
 private static void runningHeavyComputation() throws Exception
 {
     Flowable<String> source = Flowable.fromCallable(
             //Create a new Flowable from a Callable (functional interface (SAM) which simply returns a value).
             () ->
             {
                 Thread.sleep(10000);
                 return "Done";
             });

     source.doOnComplete(() -> System.out.println("Complete computation"));

    //Run the Flowable using the IO Scheduler. This Scheduler uses a cached
    // thread pool which is good for I/O (e.g., reading and writing to disk or network transfers).
     Flowable<String> backGround = source.subscribeOn(Schedulers.io());

    //Observe the results of the Flowable using a single-threaded Scheduler.
     Flowable<String> foreground = backGround.subscribeOn(Schedulers.single());

     // Finally, subscribe to the resulting foreground Flowable to initiate the flow and print the results
     // to standard out. The result of calling runComputation() will be “Done” printed after one second.
     foreground.subscribe(System.out::println, Throwable::printStackTrace);
 }

 //Publisher
 //For nontrivial problems, you might need to create your own Publisher. You would only do this
 // if you wanted fine control over the request/response nature of Reactive Streams,
 // and it is not necessary to use RxJava.
    private static void writeFile(File file)
    {
        try(PrintWriter pw = new PrintWriter(file))
        {
            Flowable.range(1, 10000)
                    .observeOn(Schedulers.newThread())
                    .blockingSubscribe(pw::println);


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

 public static void main(String[] args) throws Exception {
     //doSquares().forEach(num -> System.out.println(num + "\n"));
     //doParallelSquare().forEach(System.out::println);
     //runningHeavyComputation();
     File file = new File("/home/pupppet/scratch/data-samples/test.txt");
     writeFile(file);
 }

}
