package org.reactive.example.reactiveflow;

import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;

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
             // or Threads the flow will run. The Scheduler returned from “computation()” takes advantage of all available
             // processors when possible.
             .observeOn(Schedulers.computation())
             .map(x -> x * x)
             .blockingSubscribe(squares::add);
     return squares;
 }

 public static void main(String[] args)
 {
     doSquares().forEach(num -> System.out.println(num + "\n"));
 }

}
