package com.github.snuffix.jobschedulerdemoapp;

import android.annotation.TargetApi;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.Build;
import android.util.Log;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class DownloadJobService extends JobService {

    public static final String URLS_KEY = "urls_key";

    private Download download;

    @Override
    public boolean onStartJob(JobParameters params) {
        download = new Download(params);
        download.start();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        download.interrupt();
        Log.i("TASK : " + params.getJobId(), "Job stopped");
        return false;
    }

    private class Download extends Thread {
        private final JobParameters params;

        Download(JobParameters params) {
            this.params = params;
        }

        @Override
        public void run() {
            Log.i("TASK : " + params.getJobId(), "Job started");
            download(params.getExtras().getStringArray(URLS_KEY), params);
            jobFinished(params, false);
            Log.i("TASK : " + params.getJobId(), "Job finished");
        }

        private void download(String[] urls, JobParameters parameters) {
            for (String url : urls) {
                try {
                    Thread.sleep(1000);
                    Log.i("TASK : " + parameters.getJobId(), String.format("Downloaded %s!", url));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}

