 
close all;
dx = 2e-6;
dy = 2e-6;
z2 = 2e-3; %same z2 as in ozcan review paper
wavelength = 850e-9;
INTENSITY = imread('Image_18.bmp');
figure;imagesc(INTENSITY);colormap gray; axis equal;
recon = angular_spectrum_method(sqrt(double(INTENSITY)), z2, dx, dy, wavelength);
figure;imagesc(abs(recon).^2);colormap gray; axis equal;
figure;imagesc(angle(recon));colormap gray; axis equal;
 
 
%CREATE MASK FOR TWIN IMAGE IN RECONSTRUCTION PLANE
temp = angular_spectrum_method(double(INTENSITY), z2, dx, dy, wavelength);
MASK = (abs(temp).^1)<22*min(min((abs(temp).^1)));%CREATE MASK
figure; imagesc(MASK);colormap gray; axis equal;
n=10; h = 1/(n^2)*ones(n,n);MASK = filter2(h,MASK); MASK = MASK>0.1*max(max(MASK));%CREATE MASK
figure; imagesc(MASK);colormap gray; axis equal;
 
%PING PONG
N=4;
recon=sqrt(double(INTENSITY));
for i=1:1:N
    recon = angular_spectrum_method(recon, z2, dx, dy, wavelength);
    figure;imagesc(abs(recon));colormap gray; axis equal; title(i);
    figure;imagesc(angle(recon));colormap gray; axis equal; title(i);
    n=25; h = 1/(n^2)*ones(n,n);temp = filter2(h,abs(recon));
    temp = temp.*(abs(MASK-1));
   
    
    recon = recon.*MASK + temp;
    recon = angular_spectrum_method((recon), -z2, dx, dy, wavelength);
    recon = sqrt(double(INTENSITY)).*exp(1i*angle(recon));
end