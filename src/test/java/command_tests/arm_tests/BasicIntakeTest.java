package command_tests.arm_tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static subsystem_tests.led_tests.utils.LEDTestUtils.checkForColorInAll;

import com.revrobotics.CANSparkMax;
import command_tests.utils.CommandTestBase;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.util.Color;
import frc.robot.Constants.IntakeConstants;
import frc.robot.commands.BasicIntakeCommand;
import frc.robot.subsystems.IntakeSubsystem;
import frc.utils.sim_utils.ColorSensorV3Wrapped;
import frc.utils.sim_utils.SparkMAXSimAddon;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BasicIntakeTest extends CommandTestBase {
  private IntakeSubsystem intakeSubsystem;

  private CANSparkMax intakeMotor;
  private BasicIntakeCommand intakeCommand;

  @BeforeEach
  public void setUp() {
    super.setUp();
    intakeSubsystem = new IntakeSubsystem();
    intakeMotor = SparkMAXSimAddon.getSparkMAX(IntakeConstants.kArmIntakeMotorCanID);
    intakeCommand = new BasicIntakeCommand(intakeSubsystem);

    commandScheduler.schedule(intakeCommand);
  }

  @AfterEach
  public void tearDown() {
    super.tearDown();
    intakeSubsystem.close();
  }

  @Test
  void testSpeed() {
    commandScheduler.run();
    // Motor shouldn't be 0 when intaking
    assertNotEquals(0, intakeMotor.get());
  }

  @Test
  void testLEDBlinking() {
    commandScheduler.run();
    Timer.delay(0.2); // let led loop do its thing
    checkForColorInAll(ledSubsystem, Color.kRed, "Color should be red when started");
    Timer.delay(0.2);
    checkForColorInAll(ledSubsystem, Color.kBlack, "Color should be closed when blinking red");
  }

  @Test
  void testCommandEnds() {
    commandScheduler.run();
    ColorSensorV3Wrapped.setRGBD(2500, 0, 0, 900);
    commandScheduler.run();
    assertEquals(true, intakeCommand.isFinished(), "Command should be finished when ending");
    assertEquals(0, intakeMotor.get(), "Motor should stop after detecting color");
  }
}
