echo off
REM STEP 1:
REM 	This creates a .vec file from positive image samples using opencv_createsamples.exe.
REM 	Parameters:
REM 		-w : width of created images
REM		-h : height of created images
REM		-num : number of positive image samples
REM		-info : the .dat file that stores all the information about the positive images (input)
REM		-vec : the vec file that it creates (output)
REM
REM STEP 2:
REM	This creates a .xml file from a positive and negative image database using opencv_haartraining.exe.
REM 	Parmeters:
REM		-mem : amount of memory allowed (RAM)
REM		-minpos : the minimum number of positive images
REM		-npos : the number of positive images
REM		-nneg : the number of negative images
REM		-data : the location (folder) that the temporary output files are stored in, and also the name of the .xml file that will be created
REM		-vec : the .vec positive images database
REM		-bg : the .dat negative images database
REM		-w : the width of the training samples that are created, must be the same as the width used in opencv_createsamples.exe
REM		-h : the height of the training samples that are created, must be the same as the height used in opencv_createsamples.exe


REM Waiting for user input to begin...
echo on
pause

set PATH=%PATH%;c:\OpenCV\opencv\build\x64\vc10\bin


echo off
REM Step 1 below
echo on
opencv_createsamples.exe -w 25 -h 25 -num 350 -info positive_info.dat -vec positive_database.vec

echo off
REM Step 2 below
echo on
opencv_haartraining.exe -mem 4096 -minpos 300 -npos 350 -nneg 1500 -data ball -vec positive_database.vec -bg negative_info.dat -w 25 -h 25


echo off
REM Keeping the window up until the user presses a key (That way we can see errors)
echo on
pause