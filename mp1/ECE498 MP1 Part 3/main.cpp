/*ECE498 MP1
Harrison Yu(yu91)
Alan Chiang (achiang3)*/

#include <iostream>
#include <fstream>
#include <string>
#include <sstream>
#include <vector>
#include <string.h>
#include <stdlib.h>
using namespace std;

int main (int argc, char *argv[]) {
	string line;
	string word;
	vector<double> timestamp;
	vector<double> accel_x;
	vector<double> accel_y;
	vector<double> accel_z;
	vector<double> gyro_x;
	vector<double> gyro_y;
	vector<double> gyro_z;
	vector<double> mag_x;
	vector<double> mag_y;
	vector<double> mag_z;
	vector<double> light_intensity;

	ifstream myfile (argv[1]);
	if (myfile.is_open())
	{
	    while (! myfile.eof() )
	    {
	    	getline(myfile, line);
	    	stringstream stream(line);
	    	int counter = 0;
	    	while(getline(stream, word, ','))
            {
                double temp = atof(word.c_str());
               	if(counter == 0)
               	{
               		word.erase(word.begin());
               		temp = atof(word.c_str());
               		timestamp.push_back(temp);
               	}
               	else if(counter == 1)
               		accel_x.push_back(temp);
               	else if(counter == 2)
               		accel_y.push_back(temp);
               	else if(counter == 3)
               		accel_z.push_back(temp);
               	else if(counter == 4)
               		gyro_x.push_back(temp);
               	else if(counter == 5)
               		gyro_y.push_back(temp);
               	else if(counter == 6)
               		gyro_z.push_back(temp);
               	else if(counter == 7)
               		mag_x.push_back(temp);
               	else if(counter == 8)
               		mag_y.push_back(temp);
               	else if(counter == 9)
               		mag_z.push_back(temp);
               	else if(counter == 10)
               	{
               		word.erase(word.end()-1);
               		temp = atof(word.c_str());
               		light_intensity.push_back(temp);
               	}
               	else
               		return 1;
               	counter++;
            }
            if(counter > 0 && counter !=11)
            {
            	timestamp.pop_back();
            }
            if(counter > 1 && counter !=11)
            {
            	accel_x.pop_back();
            }
            if(counter > 2 && counter !=11)
            {
            	accel_y.pop_back();
            }
            if(counter > 3 && counter !=11)
            {
            	accel_z.pop_back();
            }
            if(counter > 4 && counter !=11)
            {
            	gyro_x.pop_back();
            }
            if(counter > 5 && counter !=11)
            {
            	gyro_y.pop_back();
            }
            if(counter > 6 && counter !=11)
            {
            	gyro_z.pop_back();
            }
            if(counter > 7 && counter !=11)
            {
            	mag_x.pop_back();
            }
            if(counter > 8 && counter !=11)
            {
            	mag_y.pop_back();
            }
            if(counter > 9 && counter !=11)
            {
            	mag_z.pop_back();
            }
            if(counter > 10 && counter !=11)
            {
            	light_intensity.pop_back();
            }
        }
        myfile.close();
	}
	else
   {
      cout << "Unable to open file";
      return 1;
   }

   int steps=0;
   int flag = 0;
   for(int i=0; i< (int)accel_z.size();i++)
   {
      if(accel_z[i] > 9.8 && flag == 0)
      {
         steps++;
         flag = 1;
      }
      else if(accel_z[i] < 9.8 && flag == 1)
      {
         steps++;
         flag = 0;
      }
   }
   cout << "The number of steps you took was approximately: "<<steps << endl;
	return 0;
}