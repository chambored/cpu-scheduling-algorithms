package utilities;

import java.util.List;

/**
 * Simulated Process class, providing data storage, access, and calculations
 * of a simulated process, given its trace tape
 * @author Nick Trimmer
 * @author Aodhan Bower
 */
public class Process {

    /**
     * The name of the process
     */
    private final String processName;
    /**
     * Stores CPU bursts mixed with IO times.
     * Structured as:
     * CPU burst, I/O time, CPU burst, I/O time, CPU burst,
     * I/O time,…….., last CPU burst
     */
    private final List<Integer> traceTape;
    /**
     * Tracks the distance through the tape the process has gotten
     */
    private int tapeCursor;
    /**
     * Tracks remaining time left on current trace tape process
     */
    private int activeProcess;
    /**
     * Total CPU burst time measured in the trace tape
     */
    private int totalCPUBurstTime;
    /**
     * Total IO time measured in the trace tape
     */
    private int totalIOTime;
    /**
     * Total time required for process execution
     */
    private int totalTime;
    /**
     * Priority level of the process, if applicable
     * Initializes to 0
     */
    private int priority;

    /**
     * Time when the process arrives at the CPU
     */
    private int arrivalTime;
    private Boolean arrivalUpdated;
    /**
     * Time when the CPU begins the process
     */
    private int startTime;
    private Boolean startUpdated;
    /**
     * The point when the CPU finishes the process
     */
    private int exitTime;
    private Boolean exitUpdated;
    /**
     * The amount of time it takes for the CPU to respond
     * to a request by the process
     */
    private int responseTime;
    
    /**
     * The total time the process spent in the ready state
     * waiting for the CPU
     */
    private int waitingTime;
    
    /**
     * The time elapsed between the arrival of the process
     * and its completion
     */
    private int turnaroundTime;
    /**
     * Indicates process completion
     */
    private boolean complete;

    /**
     * Process constructor
     * @param processName the name of the process
     * @param traceTape the realtime process trace tape
     *                  detailing CPU bursts and IO waits
     */
    public Process(String processName, List<Integer> traceTape) {
        this.processName = processName;
        this.traceTape = traceTape;
        this.tapeCursor = 0;
        this.activeProcess = traceTape.get(0);
        this.priority = 0;
        this.complete = false;
        this.arrivalUpdated = false;
        this.startUpdated = false;
        this.exitUpdated = false;
        calculateBurstTimes();
    }

    /**
     * Calculates CPU burst times and IO wait times
     * from the trace tape
     */
    private void calculateBurstTimes() {
        for (int i = 0; i < traceTape.size(); i++) {
            if (i % 2 == 0) {
                this.totalCPUBurstTime += traceTape.get(i);
            } else {
                this.totalIOTime += traceTape.get(i);
            }
        }
        this.totalTime = totalCPUBurstTime + totalIOTime;
    }

    /**
     * Progresses the tape cursor to signify that the most recent item has been completed.
     * updates the activeProcess to the new item
     * @return true if there is another item, false if the process is complete.
     */
    public boolean nextTapeItem(){
        if(tapeCursor + 1 == traceTape.size()){
            complete = true;
            return false;
        }
        else{
            activeProcess = traceTape.get(++tapeCursor);
            return true;
        }
    }

    /**
     * Lowers the value of the active trace tape process by 1
     * continues to the next item if it reaches 0
     * @return true if the current item is finished, false if not
     */
    public boolean decrementActiveProcess(){
        if(--activeProcess == 0){
           nextTapeItem();
           return true;
        }
        return false;

    }

    /**
     * Retrieves the remaining time for active trace tape process
     * @return the time remaining to complete the active trace tape process
     */
    public int getActiveProcessTimeRemaining(){
        return activeProcess;
    }

    /**
     * Retrieves the process name
     * @return the process name
     */
    public String getProcessName() {
        return processName;
    }

    /**
     * Retrieves the process priority level
     * @return the process priority level
     */
    public int getPriority() {
        return priority;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    /**
     * Retrieves the total CPU burst time measured in the
     * trace tape
     * @return the process total CPU burst time
     */
    public int getTotalCPUBurstTime() {
        return totalCPUBurstTime;
    }

    /**
     * Retrieves the total IO wait time measured
     * in the trace tape
     * @return the process total IO wait time
     */
    public int getTotalIOTime() {
        return totalIOTime;
    }

    /**
     * Retrieves the total time for process execution
     * @return total process time
     */
    public int getTotalTime() {
        return totalTime;
    }

    /**
     * Retrieves process completion status
     * @return process completion flag
     */
    public boolean getCompletion(){
        return complete;
    }

    /**
     * Updates the process priority level
     * @param priority the new process priority level
     */
    public void setPriority(int priority) {
        this.priority = priority;
    }

    /**
     * Sets the time the process arrives at the CPU
     * @param arrivalTime the time the process arrives at the CPU
     */
    public void setArrivalTime(int arrivalTime) {
        this.arrivalTime = arrivalTime;
        arrivalUpdated = true;
    }

    /**
     * Sets the time the CPU begins the process
     * @param startTime the process start time
     */
    public void setStartTime(int startTime) {
        if(!startUpdated){
            this.startTime = startTime;
            startUpdated = true;
        }

    }

    /**
     * Sets the time the process is completed by the CPU
     * @param exitTime the time the process is completed by the CPU
     */
    public void setExitTime(int exitTime) throws Exception {
        this.exitTime = exitTime;
        exitUpdated = true;
        generatePerformanceStatistics();
    }

    /**
     * Generates process execution statistics
     * Prerequisites are arrival time being set
     * and exit time being set
     * @throws Exception If either prerequisite is not met, throw
     */
    public void generatePerformanceStatistics() throws Exception {
        if (!arrivalUpdated || !exitUpdated || !startUpdated) {
            throw new Exception("One or more fields have not been set yet.");
        }
        setResponseTime();
        setWaitingTime();
        setTurnaroundTime();
    }

    /**
     * Retrieves the time elapsed between the start of the process
     * and the arrival of the process
     * @return the CPU response time
     */
    public int getResponseTime() {
        return responseTime;
    }

    /**
     * Retrieves the time the process waited in the
     * ready state
     * @return the process waiting time
     */
    public int getWaitingTime() {
        return waitingTime;
    }

    /**
     * Retrieves the amount of time between process arrival
     * at the CPU and its completion
     * @return the process turnaround time
     */
    public int getTurnaroundTime() {
        return turnaroundTime;
    }

    /**
     * Sets the response time, the amount of time it takes for the
     * CPU to respond to a request made by the process
     */
    private void setResponseTime() {
        this.responseTime = this.startTime - this.arrivalTime;
    }

    /**
     * Sets the total time the process spent in the ready state
     * waiting for the CPU
     */
    private void setWaitingTime() {
        this.waitingTime = this.exitTime - this.arrivalTime - this.totalCPUBurstTime - this.totalIOTime;
    }

    /**
     * Sets the time elapsed between the arrival of the process
     * and its completion
     */
    private void setTurnaroundTime() {
        this.turnaroundTime = this.exitTime - this.arrivalTime;
    }

    /**
     * Resets all process metrics
     */
    public void reset() {
        this.activeProcess = traceTape.get(0);
        this.arrivalTime = 0;
        this.responseTime = 0;
        this.waitingTime = 0;
        this.turnaroundTime = 0;
        this.exitTime = 0;
        this.startTime = 0;
        this.complete = false;
        this.tapeCursor = 0;
        this.startUpdated = false;
        this.arrivalUpdated = false;
        this.exitUpdated = false;
    }
}