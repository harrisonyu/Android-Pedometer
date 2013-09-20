#include <iostream>
#include <fstream>
#include <string>
#include <sstream>
#include <vector>
#include <string.h>
#include <stdlib.h>
using namespace std;

int main () {
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

	ifstream myfile ("mp1_stage1.csv");
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
	else cout << "Unable to open file";

	for(int i=0; i < (int)timestamp.size(); i++)
	{
		cout << i << " "<< timestamp[i] << ", " << accel_x[i] << ", " << accel_y[i] << ", " << accel_z[i];
		cout << ", " << gyro_x[i] << ", " << gyro_y[i] << ", " << gyro_z[i];
		cout << ", " << mag_x[i] << ", " << mag_y[i] << ", " << mag_z[i] << ", " << light_intensity[i] << endl;
	}
	return 0;
}