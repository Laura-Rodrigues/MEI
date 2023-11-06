setDim = function()

	local dims = {}
	local level = {}
	
	getAttr("RENDERER", "CURRENT", "Level", 0, level)
	dims[1] = 128 / (2^level[1])
	setAttr("PASS", "voxels#computeCubePos", "DIM_X", 0, dims)
	setAttr("PASS", "voxels#computeCubePos", "DIM_Y", 0, dims)
	setAttr("PASS", "voxels#computeCubePos", "DIM_Z", 0, dims)
end

test = function() 

	local f = {}
	local g = {}
	local h = {}
	local k = {}
	getAttr("PASS", "voxels#mipmap", "GridLevels", 0, h);
	getAttr("PASS", "voxels#mipmap", "GridCurrentLevel",0,f)
	
	if f[1] == h[1] then
		f[1] = 0
		setAttr("PASS", "voxels#mipmap", "GridCurrentLevel",0,f)
		return false
	else
		f[1] = f[1] + 1
		setAttr("PASS", "voxels#mipmap", "GridCurrentLevel",0,f)

		k[1] = h[1] - f[1];
		if k[1] == 0 then
			g[1] = 1;
		else g[1] = 2;	
			for i=2,k[1] do
				g[1] = g[1] * 2
			end	
		end
		setAttr("PASS", "voxels#mipmap", "DIM_X", 0, g);
		setAttr("PASS", "voxels#mipmap", "DIM_Y", 0, g);
		setAttr("PASS", "voxels#mipmap", "DIM_Z", 0, g);

		setAttr("IMAGE_TEXTURE", "Voxels::mipmap", "LEVEL", 1, f);
		return true
	end
end
