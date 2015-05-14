# JobSchedulerDemo

Simple app to test new Android JobScheduler API posibillities

![GitHub Logo](./images/demo.gif)

## Available constraints for executing JobInfo

<ul>
 <li>Job id</li>
 <li>Minimum latency in milliseconds (setMinimumLatency method)</li>
 <li>Deadline in milliseconds (setOverrideDeadline method)</li>
 <li>Persists after reboot (setPersisted method)</li>
 <li>Requires charging (setRequiresCharging method)</li>
 <li>Requires device idle (setRequiresDeviceIdle method)</li>
 <li>Network type (setRequiredNetworkType method)</li>
</ul>

## Managing jobs 

<ul>
 <li>Starting job</li>
 <li>Cancelling all pending jobs</li>
 <li>Displaying list of pending jobs</li>
</ul>
