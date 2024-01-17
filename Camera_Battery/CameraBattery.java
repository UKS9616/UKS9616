package 1;

/** 
 * This class keeps track of battery's charge level, battery capacity, and checks if the battery is connected
 * in camera or not.
 * 
 * @author shres
 *
 */
public class CameraBattery {
	
	//* constant number */
	public static final int NUM_CHARGER_SETTINGS = 4;
	
	//* The rate at which the camera battery is charged. */ 
	public static final double CHARGE_RATE = 2.0;
	
	//* constant value */
	public static final double DEFAULT_CAMERA_POWER_CONSUMPTION = 1.0;
	
	//* calculates and returns charge in minutes */ 
	private double charge;
	
	//* Current charge of the battery. */
	private double BatteryCharge;
	
	//* The maximum capacity of the battery. */
	private double BatteryCapacity;
	
	//* Amount of power drained in a b battery. */
	private double Drain = 0.0;
	
	//* The total charge added to a battery. */
	private double cameraCharge = 0.0;
	
	//* amount of external charge */
	private double externalCharge;	
	
	//* Total drain */
	private double TotalDrain;	
	
	//* Stores the value of power consumption of the camera. */
	private double CameraPowerConsumption = DEFAULT_CAMERA_POWER_CONSUMPTION;
	
	//* The current charging rate of battery. */
	private int ChargerSetting;
	
	//* The state of battery(connected or not). */
	private int BatteryConnected;
	
	//* Keeps track if the external battery is connected or not. */
	private int ExternalBatteryConnected;
	
	//* Stores the value of previous charge level. */
	private double previousCharge;
	
	
	/**	Initializes instant variables of the class
	 * 
	 * @param batteryStartingCharge
	 * @param batteryCapacity
	 */	
	public CameraBattery(double batteryStartingCharge, double batteryCapacity) {
		BatteryCharge = Math.min(batteryStartingCharge, batteryCapacity);
		BatteryCapacity = batteryCapacity;
		BatteryConnected = 0;
		
	}
	/* The charge setting increases by one when the user press the setting button one time on the external charger
	 * 
	 */
	
	public void buttonPress() {
		ChargerSetting = ChargerSetting + 1;
		ChargerSetting = ChargerSetting % NUM_CHARGER_SETTINGS;
	}
	
	/*
	 * Calculates camera charge for given number of minutes
	 * @param 
	 * @return the amount of time camera has gained
	 */
	public double cameraCharge(double minutes) {
		charge = Math.min(minutes * CHARGE_RATE * BatteryConnected, BatteryCapacity - BatteryCharge);
		BatteryCharge = Math.min(BatteryCharge + charge, BatteryCapacity);
		cameraCharge = BatteryCharge * BatteryConnected;
		return charge;
	}
	
	/*
	 * Drains the battery connected to the camera for a given number of minutes when the battery is still
	 * connected to the camera. 
	 * @param minutes the amount of time in minutes when the camera is used
	 * @return the amount of charge drained in a battery when it is in the camera
	 */
	public double drain(double minutes) {
		Drain = Math.min(minutes* CameraPowerConsumption * BatteryConnected,  BatteryCharge);
		TotalDrain += Drain;
		BatteryCharge = Math.max(0, BatteryCharge - Drain);
		cameraCharge = BatteryCharge * BatteryConnected;
		return Drain;
	}
	
	/*
	 * Calculates the additional charge received by the battery from external source for given number
	 * of minutes
	 * @param minutes the duration in which the battery is charged from an external source
	 * @return calculates the charge received by the battery when camera battery is externally charged
	 */
	public double externalCharge(double minutes) {
		externalCharge = minutes * ChargerSetting * CHARGE_RATE;
		double previousCharge = BatteryCharge;
		BatteryCharge = Math.min(BatteryCharge + externalCharge, BatteryCapacity);
		cameraCharge = BatteryCharge * BatteryConnected;
		return BatteryCharge - previousCharge;
	}
	
	/* Resets the battery monitor by setting total drain back to zero. 	 */
	public void resetBatteryMonitor() {
		TotalDrain = 0;		
	}
	
	/* @return total capacity of camera battery */
	public double getBatteryCapacity() {
		
		return BatteryCapacity;
	}
	
	// @return current charge of battery
	public double getBatteryCharge() {
		
		return BatteryCharge;
		
	}
	
	/* @return current charge of camera's battery
	 * 
	 */
	public double getCameraCharge() {
		
		return cameraCharge;
	}
	
	
	/*
	 * @return current power consumption of camera
	 */
	public double getCameraPowerConsumption() {
		
		return CameraPowerConsumption;
	}
	
	/*
	 * @return the current charger setting as integer value
	 */
	public int getChargerSetting() {
		
		return ChargerSetting;
	}
	
	
	/*
	 * @return total amount of battery drained after since resetting battery monitor
	 */
	public double getTotalDrain() {
		
		return TotalDrain;
		
	}
	
	/*
	 * Sets ExternalBatteryConnected to 1 indicating the external battery is connected
	 */
	public void moveBatteryExternal() {
		ExternalBatteryConnected = 1;
	}
	
	/* 
	 * updates variables to represent the move
	 */
	public void moveBatteryCamera() {
		BatteryConnected = 1;
		cameraCharge -= CameraPowerConsumption;
		
	}
	
	/*
	 * Set  variable BatteryConnected to 0 indicating battery is removed from the camera.
	 */
	public void removeBattery() {
		BatteryConnected = 0;
	    cameraCharge = 0;
	}
	
	/*
	 * @param cameraPowerConsuption the new power consumption of the camera
	 */
	public void setCameraPowerConsumption(double cameraPowerconsumption) {
		this.CameraPowerConsumption = cameraPowerconsumption;
		
	}	
	
	
	

}
