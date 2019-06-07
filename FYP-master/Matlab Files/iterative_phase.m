close all;
dx = 1.6e-6;
dy = 1.6e-6;
z2= -0600e-6;
wavelength = 579e-9;
INTENSITY = imread('slide5.tif');
REFERENCE = imread('raw.tif');
if true   
   row=1024;  col=1024;
%fin=fopen('Bare Sensor.raw','r');
%fin=fopen('Image_11.raw','r');
%fin=fopen('Pic two.raw','r');
%I=fread(fin,row*col,'uint8=>uint8'); 
%INTENSITY=reshape(I,row,col);
%INTENSITY=INTENSITY';
figure;imagesc(INTENSITY);colormap gray; axis equal;
end

%   N=30;
%   for i=1:1:N
%       recon = angular_spectrum_method(sqrt(double(INTENSITY)), -z2, dx, dy, wavelength);
%       figure;imagesc(abs(recon(1:200,1:200).^2));colormap gray; axis equal;title(z2*1000);
%       z2 = z2 + 0.1e-3;
%   end


 
%CREATE MASK FOR TWIN IMAGE IN RECONSTRUCTION PLANE
temp = angular_spectrum_method(sqrt(double(INTENSITY)), -z2, dx, dy, wavelength);
MASK = (abs(temp).^2)<2*min(min((abs(temp).^2)));%CREATE MASK
%figure; imagesc(MASK);colormap gray; axis equal;
n=10; h = 1/(n^2)*ones(n,n);MASK = filter2(h,MASK); MASK = MASK>0.1*max(max(MASK));%CREATE MASK
%figure; imagesc(MASK);colormap gray; axis equal;




 
%PING PONG
N=10;
recon=sqrt(double(INTENSITY));
ref= angular_spectrum_method(sqrt(double(REFERENCE)),-z2,dx,dy,wavelength);

for i=1:1:N
    recon = angular_spectrum_method(recon, -z2, dx, dy, wavelength);
    %figure;imagesc(abs(recon));colormap gray; axis equal; title(i);
    %figure;imagesc(angle(recon));colormap gray; axis equal; title(i);
    n=25; h = 1/(n^2)*ones(n,n);ref = filter2(h,abs(recon));

   
    
    recon = recon.*MASK + ref*(abs(MASK-1));
    recon = angular_spectrum_method((recon), z2, dx, dy, wavelength);
    recon = sqrt(double(INTENSITY)).*exp(1i*angle(recon));
end

    recon = angular_spectrum_method(recon, -z2, dx, dy, wavelength);
    figure;imagesc(abs(recon));colormap gray; axis equal; title('final');
    %figure;imagesc(angle(recon));colormap gray; axis equal; title('final');