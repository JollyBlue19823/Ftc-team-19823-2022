package org.firstinspires.ftc.teamcode.maths;

public class swerveMaths {

    //where swerve math will be done
    public double[] Math(double forward, double strafe, double rotate, double imu, boolean fieldcentrictoggle){

        forward*=-1;

        //rotate vectors by imu heading for field centric (if toggled on)
        double strafe1 = 0;
        double forward1 = 0;
        if(fieldcentrictoggle == true) {
            strafe1 = Math.cos(Math.toRadians(imu)) * strafe - Math.sin(Math.toRadians(imu)) * forward;
            forward1 = Math.sin(Math.toRadians(imu)) * strafe + Math.cos(Math.toRadians(imu)) * forward;
        }
        else{
            strafe1 = strafe;
            forward1 = forward;
        }

        //the joystick values (after rotated) converted into vectors (split in x and y) that are wheel specific, displacement vectors per wheel also
        double mod1strafe = strafe1-rotate*1;
        double mod2strafe = strafe1-rotate*-0.7;
        double mod3strafe = strafe1-rotate*-0.7;

        double mod1forward = forward1; //top wheel at x 0
        double mod2forward = forward1+rotate*1;
        double mod3forward = forward1+rotate*-1;

        //extracting the length of our wheel specific vectors (speed)
        double mod1speed = Math.sqrt((mod1strafe * mod1strafe) + (mod1forward * mod1forward));
        double mod2speed = Math.sqrt((mod2strafe * mod2strafe) + (mod2forward * mod2forward));
        double mod3speed = Math.sqrt((mod3strafe * mod3strafe) + (mod3forward * mod3forward));

        //make sure that speed values are scaled properly (none go above 1)
        double max1 = Math.max(( Math.abs(mod2speed)),  Math.abs(mod3speed));
        double maxi = Math.max((max1), Math.abs(mod1speed));
        if( Math.abs(maxi)>1) {
            mod3speed /= Math.abs(maxi);
            mod2speed /= Math.abs(maxi);
            mod1speed /= Math.abs(maxi);
        }

        //extracting the angle of our wheel specific vectors (angle)
        double mod1angle = Math.atan2(mod1strafe, mod1forward)*180 / Math.PI;
        double mod2angle = Math.atan2(mod2strafe, mod2forward)*180 / Math.PI;
        double mod3angle = Math.atan2(mod3strafe, mod3forward)*180 / Math.PI;

        //put our outputs into an array
        double[] output = {mod1speed,mod2speed,mod3speed,mod1angle,mod2angle,mod3angle};

        return output;
    }
}
