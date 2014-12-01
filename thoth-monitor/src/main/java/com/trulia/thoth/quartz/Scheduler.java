package com.trulia.thoth.quartz;

import com.trulia.thoth.monitor.AvailableMonitors;
import com.trulia.thoth.pojo.ServerDetail;
import com.trulia.thoth.util.IgnoredServers;
import com.trulia.thoth.util.ServerCache;
import com.trulia.thoth.utility.Mailer;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

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

  @Value("${thoth.monitor.predictor.uri}")
  private String thothPredictorUri;
  @Value("${thoth.monitor.predictor.enabled}")
  private boolean isThothPredictorEnabled;
  @Value("${thoth.monitor.predictor.health.score.threshold}")
  private String thothPredictorHealthScoreThreshold;
  @Autowired
  private AvailableMonitors availableMonitors;
  @Autowired
  private Mailer mailer;

  public void init() throws SchedulerException {
    JobDetail workerJob = JobBuilder.newJob(MonitorJob.class)
            .withIdentity("monitorJob", "group1").build();
    Trigger workerTrigger = TriggerBuilder
            .newTrigger()
            .withIdentity("monitorTrigger", "group1")
            .withSchedule(
                    CronScheduleBuilder.cronSchedule(quartzSchedule))
            .build();

    //Schedule it
    org.quartz.Scheduler scheduler = new StdSchedulerFactory().getScheduler();
    scheduler.start();
    scheduler.getContext().put("thothIndexURI", thothIndexURI);
    scheduler.getContext().put("serverCache", serverCache);
    this.ignoredServerDetails = new IgnoredServers(ignoredServers).getIgnoredServersDetail();
    scheduler.getContext().put("ignoredServers", ignoredServerDetails);
    scheduler.getContext().put("isPredictorMonitoringEnabled", isThothPredictorEnabled);
    scheduler.getContext().put("predictorMonitorUrl", thothPredictorUri);
    scheduler.getContext().put("predictorMonitorHealthScoreThreshold", thothPredictorHealthScoreThreshold);
    scheduler.getContext().put("availableMonitors", availableMonitors);
    scheduler.getContext().put("mailer", mailer);
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