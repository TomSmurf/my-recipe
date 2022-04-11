export interface ThreadDump {
  threads: Thread[];
}

export interface Thread {
  threadName: string;
  threadId: number;
  blockedTime: number;
  blockedCount: number;
  waitedTime: number;
  waitedCount: number;
  lockName: string | null;
  lockOwnerId: number;
  lockOwnerName: string | null;
  daemon: boolean;
  inNative: boolean;
  suspended: boolean;
  threadState: ThreadState;
  priority: number;
  stackTrace: StackTrace[];
  lockedMonitors: LockedMonitor[];
  lockedSynchronizers: string[];
  lockInfo: LockInfo | null;
  // custom field for showing-hiding thread dump
  showThreadDump?: boolean;
}

export interface LockInfo {
  className: string;
  identityHashCode: number;
}

export interface LockedMonitor {
  className: string;
  identityHashCode: number;
  lockedStackDepth: number;
  lockedStackFrame: StackTrace;
}

export interface StackTrace {
  classLoaderName: string | null;
  moduleName: string | null;
  moduleVersion: string | null;
  methodName: string;
  fileName: string;
  lineNumber: number;
  className: string;
  nativeMethod: boolean;
}

export enum ThreadState {
  Runnable = 'RUNNABLE',
  TimedWaiting = 'TIMED_WAITING',
  Waiting = 'WAITING',
  Blocked = 'BLOCKED',
  New = 'NEW',
  Terminated = 'TERMINATED',
}
