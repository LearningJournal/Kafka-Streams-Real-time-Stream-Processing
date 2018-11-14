/*
 * Copyright (c) 2018. Prashant Kumar Pandey
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */

package guru.learningjournal.kafka.examples;

public class ProducerDemo {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Required topic name" +
                    "and message key");
            System.exit(1);
        }
        SimpleProducer producerThread = new SimpleProducer(
                args[0].trim(),
                Integer.valueOf(args[1]));
        producerThread.start();
    }
}