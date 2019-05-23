package com.example.demo.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutures;
import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.cloud.pubsub.v1.SubscriptionAdminClient;
import com.google.cloud.pubsub.v1.SubscriptionAdminClient.ListSubscriptionsPagedResponse;
import com.google.cloud.pubsub.v1.TopicAdminClient;
import com.google.cloud.pubsub.v1.TopicAdminClient.ListTopicsPagedResponse;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.ListSubscriptionsRequest;
import com.google.pubsub.v1.ListTopicsRequest;
import com.google.pubsub.v1.ProjectName;
import com.google.pubsub.v1.ProjectSubscriptionName;
import com.google.pubsub.v1.ProjectTopicName;
import com.google.pubsub.v1.PubsubMessage;
import com.google.pubsub.v1.PushConfig;
import com.google.pubsub.v1.Subscription;
import com.google.pubsub.v1.Topic;

@SpringBootApplication
@RestController
public class PubSubController1 {


    // use the default project id
    private static final String PROJECT_ID = "sample-001-240716"; //ServiceOptions.getDefaultProjectId();


    private static final BlockingQueue<PubsubMessage> messages = new LinkedBlockingDeque<>();


    public static void main(String[] args) {
        SpringApplication.run(PubSubController1.class, args);
    }

    @RequestMapping(method=RequestMethod.GET,value="/publish")
    public void publish(@RequestParam String tname,@RequestParam String messageToPublish) throws Exception {

        // topic id, eg. "my-topic"
        String topicId = getTopicid(tname);
        int messageCount = 5;
        ProjectTopicName topicName = ProjectTopicName.of(PROJECT_ID, topicId);
        Publisher publisher = null;
        List<ApiFuture<String>> futures = new ArrayList<>();

        try {
            // Create a publisher instance with default settings bound to the topic
            publisher = Publisher.newBuilder(topicName).build();

            for (int i = 0; i < messageCount; i++) {
                String message =messageToPublish +"::::::::::------------------>>>>>>>>>>>" + i;

                // convert message to bytes
                ByteString data = ByteString.copyFromUtf8(message);
                PubsubMessage pubsubMessage = PubsubMessage.newBuilder()
                        .setData(data)
                        .build();

                // Schedule a message to be published. Messages are automatically batched.
                ApiFuture<String> future = publisher.publish(pubsubMessage);
                futures.add(future);
            }
        }finally {
            // Wait on any pending requests
            List<String> messageIds = ApiFutures.allAsList(futures).get();

            for (String messageId : messageIds) {
                System.out.println(messageId);
            }

            if (publisher != null) {
                // When finished with the publisher, shutdown to free up resources.
                publisher.shutdown();
            }
        }
    }

    @RequestMapping(method=RequestMethod.GET,value="/receive")
    public void receiveMessage(@RequestParam String topicInfo,@RequestParam String subscriptionInfo) throws InterruptedException, IOException {


        // set subscriber id, eg. my-sub
        String subscriptionId = createSubscription(topicInfo,subscriptionInfo);
        ProjectSubscriptionName subscriptionName = ProjectSubscriptionName.of(
                PROJECT_ID, subscriptionId);
        Subscriber subscriber = null;

        try {
            // create a subscriber bound to the asynchronous message receiver
            subscriber =
                    Subscriber.newBuilder(subscriptionName, new MessageReceiverExample()).build();
            subscriber.startAsync().awaitRunning();
            // Continue to listen to messages
            while (true) {
                PubsubMessage message = messages.take();
                System.out.println("Message Id: " + message.getMessageId());
                System.out.println("Data: " + message.getData().toStringUtf8());
            }
        } finally {
            if (subscriber != null) {
                subscriber.stopAsync();
            }
        }
    }


    static class MessageReceiverExample implements MessageReceiver {

        @Override
        public void receiveMessage(PubsubMessage message, AckReplyConsumer consumer) {
            messages.offer(message);
            consumer.ack();
            System.out.println("Consumer Acknowldge................................");
        }
    }

    @RequestMapping(method=RequestMethod.GET,value="/createTopicAndSubscription")
    public void createTopicAndPublish(@RequestParam String topicInfo,@RequestParam String subscriptionInfo) throws Exception {
        String createdTopicName = "";
        String createdSubscription= "";
        if(topicInfo != null && !"".equals(topicInfo)) {
            createdTopicName = getTopicid(topicInfo);
            if(subscriptionInfo != null && !"".equals(subscriptionInfo)) {
                createdSubscription = createSubscription(createdTopicName,subscriptionInfo);
            }
        }
    }

    public String getTopicid(String givenTopic) throws Exception {

        boolean isTopicExists = false;
        try (TopicAdminClient topicAdminClient = TopicAdminClient.create()) {
            ListTopicsRequest listTopicsRequest =ListTopicsRequest.newBuilder().setProject(ProjectName.format(PROJECT_ID)).build();
            ListTopicsPagedResponse response = topicAdminClient.listTopics(listTopicsRequest);
            Iterable<Topic> topics = response.iterateAll();
            for (Topic topic : topics) {
                if(topic.getName().contains(givenTopic)) {
                    isTopicExists=true;
                    break;
                }
            }
        }


        if(!isTopicExists) {
            ProjectTopicName topic = ProjectTopicName.of(PROJECT_ID, givenTopic);
            try (TopicAdminClient topicAdminClient = TopicAdminClient.create()) {
                Topic topicObj = topicAdminClient.createTopic(topic);
                String name = topicObj.getName();

                String[] pathTockens = name.split("/");

                return pathTockens[pathTockens.length-1];
            }
            catch(Exception e) {
                throw new Exception("Error while Creating the Topic ------:"+e.getMessage());
            }
        }
        return givenTopic;
    }

    public String createSubscription(String topicName,String subscriptionInfo) throws IOException {
        boolean isSubExists = false;
        try (SubscriptionAdminClient subscriptionAdminClient = SubscriptionAdminClient.create()) {
            ListSubscriptionsRequest listSubscriptionsRequest =ListSubscriptionsRequest.newBuilder().setProject(ProjectName.of(PROJECT_ID).toString()).build();
            ListSubscriptionsPagedResponse response = subscriptionAdminClient.listSubscriptions(listSubscriptionsRequest);
            Iterable<Subscription> subscriptions = response.iterateAll();
            for (Subscription subscription : subscriptions) {
                // do something with the subscription
                if(subscription.getName().contains(subscriptionInfo)) {
                    isSubExists=true;
                }
            }
        }
        if(!isSubExists) {
            ProjectTopicName topic = ProjectTopicName.of(PROJECT_ID, topicName);
            ProjectSubscriptionName subscription = ProjectSubscriptionName.of(PROJECT_ID, subscriptionInfo);

            try (SubscriptionAdminClient subscriptionAdminClient = SubscriptionAdminClient.create()) {
                Subscription sbc = subscriptionAdminClient.createSubscription(subscription, topic, PushConfig.getDefaultInstance(), 0);
                String name = sbc.getName();

                String[] pathTockens = name.split("/");
                return pathTockens[pathTockens.length-1];
            }
        }
        return subscriptionInfo;

    }
}
