import com.jobs.TestJob;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Date;

/**
 * Created by misty on 2018/3/22.
 */
public class Test {
    public void go() throws Throwable {

        SchedulerFactory factory = new StdSchedulerFactory();
        // 从工厂里面拿到一个scheduler实例
        Scheduler scheduler = factory.getScheduler();

        // 计算任务的开始时间，DateBuilder.evenMinuteDate方法是取下一个整数分钟
//        Date runTime = DateBuilder.evenMinuteDate(new Date());
        Date runTime = DateBuilder.nextGivenSecondDate(null, 13);
        // 真正执行的任务并不是Job接口的实例，而是用反射的方式实例化的一个JobDetail实例
        JobDetail job = JobBuilder.newJob(TestJob.class).withIdentity("job1", "group1").build();
        // 定义一个触发器，startAt方法定义了任务应当开始的时间
        Trigger trigger = TriggerBuilder.newTrigger().withIdentity("trigger1", "group1")
//                .withSchedule(SimpleScheduleBuilder.simpleSchedule().repeatForever().withIntervalInSeconds(3))
                .startAt(runTime).build();
        // 将任务和Trigger放入scheduler
        scheduler.scheduleJob(job, trigger);

//        runTime = DateBuilder.nextGivenSecondDate(null, 13);
        //将一个Job实现作为另外一个任务注册到scheduler，注意名字要区分
        //名字作为job唯一标识
//        job = JobBuilder.newJob(TestJob.class).withIdentity("job2", "group1").build();
//        trigger = TriggerBuilder.newTrigger().withIdentity("trigger2", "group1").startAt(runTime).build();
//        scheduler.scheduleJob(job, trigger);

//        runTime = DateBuilder.nextGivenSecondDate(null, 13);
        //第一次执行之后每隔3s执行一次，重复5次，共执行6次
//        job = JobBuilder.newJob(TestJob.class).withIdentity("job3", "group1").build();
//        trigger = TriggerBuilder.newTrigger().withIdentity("trigger3", "group1")
//                .withSchedule(SimpleScheduleBuilder.simpleSchedule().repeatForever().withIntervalInSeconds(3))
//                .startAt(runTime).build();
//        scheduler.scheduleJob(job, trigger);

        job = JobBuilder.newJob(TestJob.class).withIdentity("job3", "group1").build();
        trigger = TriggerBuilder.newTrigger().withIdentity("trigger3", "group1")
                .withSchedule(CronScheduleBuilder.cronSchedule("0 50/1 10 ? * *"))
                .startAt(runTime).build();
        scheduler.scheduleJob(job, trigger);

        scheduler.start();

        try {
            // 等待65秒，保证下一个整数分钟出现，这里注意，如果主线程停止，任务是不会执行的
            Thread.sleep(20L * 1000L);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // scheduler结束
//        scheduler.shutdown(true);
    }

    public static void main(String[] args) throws Throwable{
//        Test test = new Test();
//        test.go();
        ApplicationContext ac = new ClassPathXmlApplicationContext("applicationContext.xml");
    }
}
