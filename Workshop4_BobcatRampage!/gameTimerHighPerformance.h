// 2005 - Martin Masek, Edith Cowan University

#include <sstream> 

class GameTimerHighPerformance
{
public:
	GameTimerHighPerformance();
	~GameTimerHighPerformance(){};
	// return time elapsed between now and when getDeltaTime was last called
	double getDeltaTime();
	double getTime();
	double getFrequency();
	double calculateFPS();

private:
	LARGE_INTEGER timerFrequency;
	LARGE_INTEGER startTime;
	int frameCount;

	double fps;
};

GameTimerHighPerformance::GameTimerHighPerformance()
{
	QueryPerformanceFrequency(&timerFrequency);
	QueryPerformanceCounter(&startTime);
	frameCount = 0;
	fps = 0;
}

double GameTimerHighPerformance::getTime()
{
	LARGE_INTEGER currentTime;
	QueryPerformanceCounter(&currentTime);
	return ((double)currentTime.QuadPart);
}
double GameTimerHighPerformance::getFrequency()
{
	return ((double)timerFrequency.QuadPart);
}

double GameTimerHighPerformance::getDeltaTime()
{
	LARGE_INTEGER currentTime;
	double secondsElapsed;
	QueryPerformanceCounter(&currentTime);
	secondsElapsed = (double)(currentTime.QuadPart - startTime.QuadPart)/(double)timerFrequency.QuadPart;
	startTime = currentTime;
	return(secondsElapsed);
}

double GameTimerHighPerformance::calculateFPS()
{
	//  Increase frame count
	double secondsElapsed;
	frameCount++;
	LARGE_INTEGER currentTime;
	QueryPerformanceCounter(&currentTime);


	//  Calculate time passed
	secondsElapsed = (double)(currentTime.QuadPart - startTime.QuadPart)/(double)timerFrequency.QuadPart;

	if(secondsElapsed > 1)
	{
		//  calculate the number of frames per second
		fps = frameCount / secondsElapsed;
		//  Set time
		startTime = currentTime;
		//  Reset frame count
		frameCount = 0;
	}
	
	return fps;
}