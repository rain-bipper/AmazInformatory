/*
package com.formalizationunit.amaz.informatory.host;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;

import androidx.annotation.Nullable;

public class UpdateService extends JobService {
    private static final int JOB_ID = 1;

    @Nullable
    private Facade mProcessor;
    @Nullable
    private CancelableCallback mCurrentJobCallback;

    public static void schedule(Context context, long intervalMillis) {
        JobScheduler jobScheduler = context.getSystemService(JobScheduler.class);
        ComponentName componentName =
                new ComponentName(context, UpdateService.class);
        JobInfo.Builder builder = new JobInfo.Builder(JOB_ID, componentName)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setRequiresBatteryNotLow(true)
                .setPeriodic(intervalMillis);
        jobScheduler.schedule(builder.build());
    }

    @Override
    public boolean onStartJob(final JobParameters params) {
        App.logger().log("<plug>", "UpdateService: starting " + params.getJobId());

        if (mProcessor == null) {
            mProcessor = new Facade(this);
        }

        mCurrentJobCallback = new CancelableCallback(() -> {
            mCurrentJobCallback = null;
            App.logger().log("<plug>", "UpdateService: async finished " +
                    params.getJobId());
            jobFinished(params, false);
        });
        mProcessor.process(mCurrentJobCallback);

        App.logger().log("<plug>", "UpdateService: started " + params.getJobId());

        // Run asynchronously.
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        cleanup();
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        App.logger().log("<plug>", "UpdateService: force stop " +
                params.getJobId());
        cleanup();
        return false;
    }

    private void cleanup() {
        if (mCurrentJobCallback != null) {
            mCurrentJobCallback.cancel();
            mCurrentJobCallback = null;
        }

        if (mProcessor != null) {
            mProcessor.destroy();
            mProcessor = null;
        }
    }

    private class CancelableCallback implements Runnable {

        @Nullable
        private Runnable mBody;

        private CancelableCallback(@Nullable final Runnable body) {
            mBody = body;
        }

        @Override
        public void run() {
            if (mBody != null) {
                mBody.run();
            }
        }

        private void cancel() {
            mBody = null;
        }
    }
}
*/