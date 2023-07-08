

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Calendar;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import javax.swing.Timer;

class DigitalClock extends JFrame{
	Map<String, JLabel> clockLabels;
    	DateTimeFormatter timeFormatter;
    	ZonedDateTime alarmTime;
    	JTextField hourField, minuteField;
    	JComboBox<String> amPmComboBox;
    	JButton setButton;
    	JLabel statusLabel;
    	TextArea txtView;
    	Container c;

	int hour;
	int minute;

	DigitalClock(){
		c = getContentPane();
		c.setLayout(null);
		c.setBackground(Color.BLACK);

		setTitle("Digital Clock");
        	setSize(600, 700);
        	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        	setVisible(true);
		setLocationRelativeTo(null);


        	JLabel hourLabel = new JLabel("Hour:");
        	c.add(hourLabel);
        	hourLabel.setBounds(50, 238, 200, 50);
        	hourLabel.setForeground(Color.WHITE);


        	hourField = new JTextField(2);
       		c.add(hourField);
        	hourField.setBounds(120, 250, 50, 30);

	        JLabel minuteLabel = new JLabel("Minute:");
	        c.add(minuteLabel);
	        minuteLabel.setBounds(50, 290, 200, 50);
        	minuteLabel.setForeground(Color.WHITE);

	        minuteField = new JTextField(2);
	        c.add(minuteField);
        	minuteField.setBounds(120, 300, 50, 30);

		JLabel amPmLabel = new JLabel("AM/PM:");
	        c.add(amPmLabel);
        	amPmLabel.setBounds(50, 340, 50, 50);
	        amPmLabel.setForeground(Color.WHITE);

        	amPmComboBox = new JComboBox<>(new String[]{"AM", "PM"});
        	c.add(amPmComboBox);
        	amPmComboBox.setBounds(120, 350, 50, 30);

        	setButton = new JButton("Set Alarm");
        	setButton.addActionListener(new ActionListener() {
        		public void actionPerformed(ActionEvent e) {
                		setAlarm();
            		}
        	});
        	c.add(setButton);
        	setButton.setBounds(100, 400, 100, 40);


		txtView = new TextArea();
		txtView.setBounds(50,450, 200,130);
		c.add(txtView);
		txtView.setEditable(false);

		clockLabels = new HashMap<>();
        	timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss a");

        	addClockLabel("New York", "America/New_York", 80, 5);
        	addClockLabel("London", "Europe/London", 350, 5);
       		addClockLabel("Tokyo", "Asia/Tokyo", 80, 100);
       		addClockLabel("Mumbai", "Asia/Kolkata", 350, 100);

        	startTimer();

	}

	public void addClockLabel(String locationName, String timeZoneId, int x, int y) {
        	JLabel locationLabel = createLocationLabel(locationName);
        	c.add(locationLabel);
        	locationLabel.setBounds(x, y, 200, 50);

        	JLabel clockLabel = createClockLabel();
        	clockLabels.put(timeZoneId, clockLabel);
        	c.add(clockLabel);
        	clockLabel.setBounds(x - 50, y + 30, 200, 50);

        	updateClockLabel(timeZoneId, clockLabel, ZonedDateTime.now());
    	}

	public JLabel createLocationLabel(String locationName) {
        	JLabel locationLabel = new JLabel(locationName);
	        locationLabel.setForeground(Color.WHITE);
        	locationLabel.setFont(new Font("Arial", Font.BOLD, 16));
        	return locationLabel;
    	}

    	public JLabel createClockLabel() {
        	JLabel clockLabel = new JLabel();
        	clockLabel.setForeground(Color.GREEN);
        	clockLabel.setFont(new Font("Arial", Font.PLAIN, 24));
        	return clockLabel;
    	}

    	public void startTimer() {
        	Timer timer = new Timer(1000, e -> {
        	    updateClocks();
        	});
        	timer.start();
    	}

    	public void updateClocks() {
        	ZonedDateTime now = ZonedDateTime.now();
		for (Map.Entry<String, JLabel> entry : clockLabels.entrySet()) {
            		String timeZoneId = entry.getKey();
            		JLabel clockLabel = entry.getValue();
            		updateClockLabel(timeZoneId, clockLabel, now);
        	}
    	}

    	public void updateClockLabel(String timeZoneId, JLabel clockLabel, ZonedDateTime currentTime) {
        	ZonedDateTime zoneTime = currentTime.withZoneSameInstant(ZoneId.of(timeZoneId));
        	String formattedTime = zoneTime.format(timeFormatter);
        	clockLabel.setText(formattedTime);
    	}

	public void setAlarm(){
		String hourText = hourField.getText();
        	String minuteText = minuteField.getText();
        	String amPm = (String) amPmComboBox.getSelectedItem();

        	if (hourText.isEmpty() || minuteText.isEmpty()) {
            		JOptionPane.showMessageDialog(c, "Please enter valid hour and minute.");
            		return;
        	} 
		if (!hourText.matches("\\d+") || !minuteText.matches("\\d+")) {
            		JOptionPane.showMessageDialog(c, "Please enter valid hour and minute.");
            		return;
        	}

        	 hour = Integer.parseInt(hourText);
        	 minute = Integer.parseInt(minuteText);

        	if (hour < 0 || hour > 12 || minute < 0 || minute > 59) {
            		JOptionPane.showMessageDialog(c, "Please enter valid hour (0-12) and minute (0-59).");
            		return;
        	}

        	JOptionPane.showMessageDialog(c, "Alarm set for " + hour + ":" + minute + " " + amPm);
        	txtView.append("Alarm set for= " + hour + ":" + minute + " " + amPm + "\n");

		Thread alarmThread = new Thread(() -> {
			while(true){
				Calendar now = Calendar.getInstance();
                 		int currentHour = now.get(Calendar.HOUR);
                    		int currentMinute = now.get(Calendar.MINUTE);
                    		int currentAmPm = now.get(Calendar.AM_PM);

                    		int alarmHour = hour;
                   		int alarmMinute = minute;
                    		int alarmAmPm = getAmPmValue(amPm);

				if (currentAmPm == Calendar.PM && alarmAmPm == Calendar.AM) {
                       			currentHour += 12;
                    		}

                    		if (currentHour == hour && currentMinute == minute && currentAmPm == alarmAmPm) {

                			SwingUtilities.invokeLater(() -> {
                    				Toolkit.getDefaultToolkit().beep();
                   			JOptionPane.showMessageDialog(null, "ALARM!");
                			});
					break;
              			}
				try {
                        		Thread.sleep(1000); // Check every second
                    		} catch (InterruptedException e) {
                       			e.printStackTrace();
                  		}
			}
			
		});
		alarmThread.start();
	}
	public int getAmPmValue(String amPm) {
        	if (amPm.equalsIgnoreCase("AM")) {
            		return Calendar.AM;
        	} else {
            		return Calendar.PM;
        	}
    	}

    	public static void main(String[] args) {
        	SwingUtilities.invokeLater(new Runnable() {
            		public void run() {
                		new DigitalClock();
            		}
        	});
    	}
}




















