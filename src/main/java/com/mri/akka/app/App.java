package com.mri.akka.app;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.actor.UntypedActorFactory;
import akka.routing.RoundRobinRouter;
import scala.concurrent.duration.Duration;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeUnit;

import akka.dispatch.OnComplete;
import akka.dispatch.OnSuccess;
import akka.dispatch.Futures;
import akka.pattern.Patterns;
import static akka.pattern.Patterns.ask;
import static akka.pattern.Patterns.ask;

import scala.concurrent.Future;
import scala.concurrent.Await;
import scala.concurrent.Promise;
import scala.concurrent.ExecutionContext;
import scala.concurrent.ExecutionContext$;

import akka.util.Timeout;
import akka.event.Logging;
import akka.event.LoggingAdapter;


public class App {

 public static void main(String args[]){
     System.out.println("\nTest app created in Akka \nPurpose: to simulate blocking and nonblocking execution\n");

     ActorSystem system = ActorSystem.create("MyApp");
     ActorRef myfriend = system.actorOf(new Props(FriendActor.class), "Friend");

     Envelope msgObj = new Envelope("Hello Dude, how is it going?");
     Timeout timeout = new Timeout(Duration.create(1000, TimeUnit.MILLISECONDS));
     Future<Object> future = Patterns.ask(myfriend, msgObj, timeout);


     // ----- (1) Rcoomended approach -----
     // 
     // this doesn't block the current thread
     // as we're using future and callback
     //
     final ExecutionContext ec = system.dispatcher();
 
     future.onComplete(new OnComplete<Object>() {
         public void onComplete(Throwable t, Object result) {
            System.out.println("[CALLBACK   ]: Handling OnComplete......");

            if ("hey mate! what's up?" == (String)result) {
                System.out.println("[CALLBACK   ]: Good that you remember me :)");
            } else {
                System.out.println("[CALLBACK   ]: Oh shame you My friend doesn't recognize me :(");
            }

	    system.shutdown();
         }
     }, ec);


     // ----- (2) Not a healthy approach -----
     //
     // this blocks current running thread
     // as we're using Await() and no callback
     //
     /*try {
         String result = (String) Await.result(future, timeout.duration());
         if ("hey mate! what's up?" == result) {
             System.out.println("[AWAIT      ]: Good that you remember me :)");
         } else {
             System.out.println("[AWAIT      ]: Oh shame you My friend doesn't recognize me :(");
         }
     } catch(Exception e){
	 System.out.println("[AWAIT      ]: Actor timeout fired!!!!");
     } finally{
         system.shutdown();
     }*/

     System.out.println("[MAIN THREAD]: IF THIS IS THE LAST MESSAGE YOU SEE, YOU'RE BLOCKING! IF NOT YOU AREN'T");; 
 }


 static class Envelope{
     private String message;

     public Envelope(String message){
        this.message = message;
     }

     public String getMessage(){
         return message;
     }
 }

 public static class FriendActor extends UntypedActor {

     private LoggingAdapter log = Logging.getLogger(getContext().system(), this);

     public void onReceive(Object message){
         if (message instanceof Envelope){
             String msg = ((Envelope)message).getMessage(); 
             //log.info("Main sent message {}", msg);
             System.out.println("[ACTOR      ]: " +  getSender().toString() + " said " +  msg);
             System.out.println("[ACTOR      ]: I'm scanning my image library to recognise you...");
	     for (long i=0L; i<100000000; i++) {}
             getSender().tell("hey mate! what's up?", self());
	 }else{
             unhandled(message);
	 }
     } 
 }

}

