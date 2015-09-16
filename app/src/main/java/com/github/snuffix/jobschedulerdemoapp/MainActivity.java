package com.github.snuffix.jobschedulerdemoapp;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class MainActivity extends ActionBarActivity {

    @InjectView(R.id.job_id)
    EditText jobIdField;

    @InjectView(R.id.amount_of_downloads)
    EditText amountOfDownloadsField;

    @InjectView(R.id.minimum_latency)
    EditText minimumLatencyField;

    @InjectView(R.id.override_deadline)
    EditText overrideDeadlineField;

    @InjectView(R.id.persists_after_reboot)
    CheckBox persistsAfterRebootCheckBox;

    @InjectView(R.id.requires_charging)
    CheckBox requiresChargingCheckBox;

    @InjectView(R.id.requires_idle)
    CheckBox requiresIdleCheckBox;

    @InjectView(R.id.network_type)
    Spinner networkTypeSpinner;

    @InjectView(R.id.pending_jobs)
    LinearLayout pendingJobsGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
    }

    @OnClick(R.id.start_job)
    public void startJob() {
        JobInfo.Builder builder = new JobInfo.Builder(getJobId(), new ComponentName(getBaseContext(), DownloadJobService.class))
                .setRequiresCharging(requiresChargingCheckBox.isChecked())
                .setPersisted(persistsAfterRebootCheckBox.isChecked())
                .setRequiresDeviceIdle(requiresIdleCheckBox.isChecked())
                .setExtras(getExtras());

        setDeadline(builder);
        setMinimumLatency(builder);
        setRequiredNetworkType(builder);

        JobScheduler scheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        int result = scheduler.schedule(builder.build());
        if (result == JobScheduler.RESULT_SUCCESS) {
            Toast.makeText(this, "Job scheduled !", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Sry, couldn't schedule your job..", Toast.LENGTH_SHORT).show();
        }

        refreshPendingJobs();
    }

    private int getJobId() {
        String jobId = jobIdField.getText().toString();
        return jobId.isEmpty() ? 0 : Integer.parseInt(jobId);
    }

    private PersistableBundle getExtras() {
        PersistableBundle bundle = new PersistableBundle();

        String downloadsAmount = amountOfDownloadsField.getText().toString();
        String[] urls = new String[downloadsAmount.isEmpty() ? 0 : Integer.parseInt(downloadsAmount)];

        for (int i = 0; i < urls.length; ++i) {
            urls[i] = "some_random_url_" + i;
        }
        bundle.putStringArray(DownloadJobService.URLS_KEY, urls);
        return bundle;
    }

    private void setDeadline(JobInfo.Builder builder) {
        String deadline = overrideDeadlineField.getText().toString();
        if (!deadline.isEmpty()) {
            builder.setOverrideDeadline(Integer.parseInt(deadline));
        }
    }

    private void setMinimumLatency(JobInfo.Builder builder) {
        String minimumLatency = minimumLatencyField.getText().toString();
        if (!minimumLatency.isEmpty()) {
            builder.setMinimumLatency(Integer.parseInt(minimumLatency));
        }
    }

    private void setRequiredNetworkType(JobInfo.Builder builder) {
        String chosenNetworkType = networkTypeSpinner.getSelectedItem().toString();

        if (chosenNetworkType.equals(getString(R.string.network_type_any))) {
            builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
        } else if (chosenNetworkType.equals(getString(R.string.network_type_unmetered))) {
            builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED);
        } else {
            builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_NONE);
        }
    }

    @OnClick(R.id.cancel_all_jobs)
    public void cancelAllJobs() {
        JobScheduler scheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        scheduler.cancelAll();
        refreshPendingJobs();
    }

    @OnClick(R.id.refresh_jobs)
    public void refreshPendingJobs() {
        pendingJobsGroup.removeAllViews();

        JobScheduler scheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);

        for (JobInfo jobInfo : scheduler.getAllPendingJobs()) {
            pendingJobsGroup.addView(new JobInfoView(getBaseContext(), jobInfo), pendingJobsGroup.getChildCount());
        }
    }

    class JobInfoView extends LinearLayout {

        @InjectView(R.id.job_id)
        TextView jobIdLabel;

        @InjectView(R.id.minimum_latency)
        TextView minimumLatencyLabel;

        @InjectView(R.id.deadline)
        TextView deadlineLabel;

        @InjectView(R.id.is_persisted)
        TextView isPersistedLabel;

        @InjectView(R.id.requires_charging)
        TextView requiresCharginLabel;

        @InjectView(R.id.requires_idle)
        TextView requiresIdleLabel;

        @InjectView(R.id.network_type)
        TextView networkTypeLabel;

        public JobInfoView(Context context, JobInfo jobInfo) {
            super(context);
            ButterKnife.inject(View.inflate(getContext(), R.layout.pending_job_view, this));
            jobIdLabel.setText("Job number : "+ jobInfo.getId());
            minimumLatencyLabel.setText("Minimum latency : "+ jobInfo.getMinLatencyMillis());
            deadlineLabel.setText("Deadline : "+ jobInfo.getMaxExecutionDelayMillis());
            isPersistedLabel.setText("Is persisted : "+ jobInfo.isPersisted());
            requiresCharginLabel.setText("Requires charging : "+ jobInfo.isRequireCharging());
            requiresIdleLabel.setText("Requires idle : "+ jobInfo.isRequireDeviceIdle());
            networkTypeLabel.setText("Network type : "+ getNetworkTypeName(jobInfo.getNetworkType()));
        }

    }

    private String getNetworkTypeName(int networkType) {
        if (networkType == JobInfo.NETWORK_TYPE_NONE) {
            return getString(R.string.network_type_none);
        } else if (networkType == JobInfo.NETWORK_TYPE_UNMETERED) {
            return getString(R.string.network_type_unmetered);
        } else {
            return getString(R.string.network_type_any);
        }
    }


}
