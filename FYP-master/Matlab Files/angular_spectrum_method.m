%   Written by Bryan Hennelly, NUIM, 22/06/17

%   The following code is based on the angular spectral method algorithm
%   outlined in Goodman
%   
%	hologram: input hologram
%   reconstruction: output reconstruction
%	distance: distance of propagation
%   pixel_x,pixel_y: CCD pixel size of recording device
%   wavelength: wavelength of light

%   All units in m

function reconstruction = angular_spectrum_method(hologram, distance, pixel_x, pixel_y, wavelength)


S = size(hologram);
ny = S(1);
nx = S(2);
[x,y]=meshgrid(ceil(-nx/2):ceil(nx/2-1),ceil(-ny/2):ceil(ny/2-1));

width_y = ny*pixel_y;
width_x = nx*pixel_x;

% Create input and output chirps to 
d_x = x*(1/(pixel_x*nx));
d_y = y*(1/(pixel_y*ny));
temp = 1-(wavelength*d_x).^2-(wavelength*d_y).^2;%figure;imagesc(temp>0);colormap gray;
Chirp = exp(  (1i*2*pi*distance/wavelength)*((temp.*(temp>=0)).^0.5)   ).*(temp>=0);
reconstruction = fftshift(ifft2(fftshift((fftshift(fft2(fftshift(hologram)))).*Chirp)));


