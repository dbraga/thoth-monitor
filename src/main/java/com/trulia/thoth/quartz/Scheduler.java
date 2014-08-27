package com.trulia.thoth.quartz;

import com.trulia.thoth.pojo.ServerDetail;
import com.trulia.thoth.util.ServerCache;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.util.ArrayList;

/**
 * User: dbraga - Date: 8/16/14
 */
public class Scheduler {

  private String thothIndexURI;
  private ServerCache serverCache;
  private String ignoredServers;
  private ArrayList<ServerDetail> ignoredServerDetails;
  private String quartzSchedule;

  private void retrieveIgnoredServerDetails(){
    ignoredServerDetails = new ArrayList<ServerDetail>();

    for (String ignoredServer: ignoredServers.split(",")){
      String[] splitted = ignoredServer.split(";");
      if (splitted.length % 4 == 0) ignoredServerDetails.add(new ServerDetail(splitted[0], splitted[3], splitted[1], splitted[2]));
    }
  }

  public void init() throws SchedulerException {

    JobDetail workerJob = JobBuilder.newJob(MonitorJob.class)
            .withIdentity("pendingWorkJob", "group1").build();
    Trigger workerTrigger = TriggerBuilder
            .newTrigger()
            .withIdentity("pendingWorkTrigger", "group1")
            .withSchedule(
                    CronScheduleBuilder.cronSchedule(quartzSchedule)) // execute this every day at midnight
            .build();

    //Schedule it
    org.quartz.Scheduler scheduler = new StdSchedulerFactory().getScheduler();
    scheduler.start();
    scheduler.getContext().put("thothIndexURI", thothIndexURI);
    scheduler.getContext().put("serverCache", serverCache);
    retrieveIgnoredServerDetails();
    scheduler.getContext().put("ignoredServers", ignoredServerDetails);


    scheduler.scheduleJob(workerJob, workerTrigger);
  }

  public void setThothIndexURI(String thothIndexURI) {
    this.thothIndexURI = thothIndexURI;
  }

  public String getThothIndexURI() {
    return thothIndexURI;
  }

  public void setServerCache(ServerCache serverCache) {
    this.serverCache = serverCache;
  }

  public ServerCache getServerCache() {
    return serverCache;
  }

  public void setIgnoredServers(String ignoredServers) {
    this.ignoredServers = ignoredServers;
  }

  public String getIgnoredServers() {
    return ignoredServers;
  }

  public void setQuartzSchedule(String quartzSchedule) {
    this.quartzSchedule = quartzSchedule;
  }

  public String getQuartzSchedule() {
    return quartzSchedule;
  }
}