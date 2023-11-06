#version 450

// uniform variables
uniform samplerCube tex_cm;
uniform float eta = 1.2;

// input variables
in vec3 n, incident;

// output variables
out vec4 color;

void main() {

    vec3 nn = normalize(n);
    vec3 ii = normalize(incident);

    vec3 refl = reflect(ii, nn);
    vec3 refl_c = texture(tex_cm, refl).rgb;

    vec3 refr = refract(ii, nn, eta);
    vec3 refr_c = texture(tex_cm, refr).rgb;

    float k = 1 - eta*eta* (1 - dot(-ii, nn) * dot(-ii, nn));

    color = vec4(mix(refl_c, refr_c, k), 1);
} 